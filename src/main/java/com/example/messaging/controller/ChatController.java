package com.example.messaging.controller;

import com.example.messaging.dto.*;
import com.example.messaging.model.*;
import com.example.messaging.repository.UserDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// Custom Exceptions
class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private UserDB userDb;

    private final Map<Integer, ChatRoom> chats = new HashMap<>();
    private int nextMsgId = 1;
    private int nextChatId = 1;

    // Exception Handlers
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error: " + ex.getMessage()));
    }

    // API Endpoints
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "Chat Messaging System Backend is running.");
    }

    @GetMapping("/friends/{userId}")
    public List<Map<String, Object>> getFriends(@PathVariable("userId") int userId) {
        User user = userDb.findById(userId);
        if (user == null) throw new ResourceNotFoundException("User not found.");

        List<Map<String, Object>> friendList = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            User friend = userDb.findById(friendId);
            if (friend != null) {
                friendList.add(Map.of(
                        "user_id", friend.getUserId(),
                        "name", friend.getName()
                ));
            }
        }
        return friendList;
    }

    @PostMapping("/friends/add")
    public Map<String, Object> addFriend(@RequestBody AddFriendData data) {
        User me = userDb.findById(data.getUserId());
        if (me == null) throw new ResourceNotFoundException("User not found.");

        User friend = userDb.findUserByUsername(data.getFriendName());
        if (friend == null) throw new ResourceNotFoundException("Friend not found.");

        if (friend.getUserId() == data.getUserId()) {
            throw new BadRequestException("You cannot add yourself.");
        }

        me.addFriend(friend.getUserId());
        friend.addFriend(me.getUserId());

        return Map.of("message", "Friend added successfully.", "friend", friend);
    }

    @PostMapping("/private-chat")
    public Map<String, Object> createPrivateChat(@RequestBody PrivateChatData data) {
        User me = userDb.findById(data.getUserId());
        User friend = userDb.findById(data.getFriendId());
        
        if (me == null || friend == null) throw new ResourceNotFoundException("User or friend not found.");
        if (!me.getFriends().contains(data.getFriendId())) throw new UnauthorizedException("You must add this user as friend first.");

        for (ChatRoom room : chats.values()) {
            if (room.getChatType().equals("private") && room.getMembers().contains(me.getUserId()) && room.getMembers().contains(friend.getUserId())) {
                return Map.of("message", "Private chat already exists.", "chat", chatToMap(room));
            }
        }

        String chatName = me.getName() + " & " + friend.getName();
        ChatRoom newChat = new ChatRoom(nextChatId++, chatName, "private", List.of(me.getUserId(), friend.getUserId()));
        chats.put(newChat.getChatId(), newChat);

        return Map.of("message", "Private chat created successfully.", "chat", chatToMap(newChat));
    }

    @PostMapping("/groups/create")
    public Map<String, Object> createGroup(@RequestBody CreateGroupData data) {
        if (userDb.findById(data.getUserId()) == null) throw new ResourceNotFoundException("User not found.");
        if (data.getGroupName() == null || data.getGroupName().trim().isEmpty()) throw new BadRequestException("Group name cannot be empty.");
        if (findGroupByName(data.getGroupName()) != null) throw new BadRequestException("Group already exists.");

        ChatRoom group = new ChatRoom(nextChatId++, data.getGroupName().trim(), "group", List.of(data.getUserId()));
        chats.put(group.getChatId(), group);

        return Map.of("message", "Group created successfully.", "chat", chatToMap(group));
    }

    @PostMapping("/groups/join-by-name")
    public Map<String, Object> joinGroupByName(@RequestBody JoinGroupData data) {
        if (userDb.findById(data.getUserId()) == null) throw new ResourceNotFoundException("User not found.");

        ChatRoom group = findGroupByName(data.getGroupName());
        if (group == null) throw new ResourceNotFoundException("Group not found.");

        group.addMember(data.getUserId());
        return Map.of("message", "Joined group successfully.", "chat", chatToMap(group));
    }

    @GetMapping("/chats")
    public List<Map<String, Object>> getChats(@RequestParam("user_id") int userId) {
        if (userDb.findById(userId) == null) throw new ResourceNotFoundException("User not found.");

        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatRoom chat : chats.values()) {
            if (chat.getMembers().contains(userId)) {
                result.add(chatToMap(chat));
            }
        }
        return result;
    }

    @GetMapping("/chats/{chatId}/messages")
    public List<Map<String, Object>> getMessages(@PathVariable("chatId") int chatId) {
        if (!chats.containsKey(chatId)) throw new ResourceNotFoundException("Chat room not found.");

        List<Map<String, Object>> result = new ArrayList<>();
        for (Message msg : chats.get(chatId).getMessages()) {
            result.add(messageToMap(msg));
        }
        return result;
    }

    @PostMapping("/chats/{chatId}/messages")
    public Map<String, Object> sendMessage(@PathVariable("chatId") int chatId, @RequestBody SendMsgData data) {
        if (!chats.containsKey(chatId)) throw new ResourceNotFoundException("Chat room not found.");

        try {
            ChatRoom chat = chats.get(chatId);
            Message newMsg = chat.addMessage(data.getSenderId(), data.getContent(), nextMsgId++);
            return Map.of("message", "Message sent successfully.", "data", messageToMap(newMsg));
        } catch (SecurityException e) {
            throw new UnauthorizedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/chats/{chatId}/messages/{msgId}")
    public Map<String, Object> editMessage(@PathVariable("chatId") int chatId, @PathVariable("msgId") int msgId, @RequestBody EditMsgData data) {
        if (!chats.containsKey(chatId)) throw new ResourceNotFoundException("Chat room not found.");

        ChatRoom chat = chats.get(chatId);
        Message msg = chat.findMessage(msgId);
        if (msg == null) throw new ResourceNotFoundException("Message not found.");
        if (msg.getSenderId() != data.getUserId()) throw new UnauthorizedException("You can only edit your own message.");

        msg.edit(data.getNewContent());
        return Map.of("message", "Message edited successfully.", "data", messageToMap(msg));
    }

    @DeleteMapping("/chats/{chatId}/messages/{msgId}")
    public Map<String, String> deleteMessage(@PathVariable("chatId") int chatId, @PathVariable("msgId") int msgId, @RequestParam("user_id") int userId) {
        if (!chats.containsKey(chatId)) throw new ResourceNotFoundException("Chat room not found.");

        ChatRoom chat = chats.get(chatId);
        Message msg = chat.findMessage(msgId);
        if (msg == null) throw new ResourceNotFoundException("Message not found.");
        if (msg.getSenderId() != userId) throw new UnauthorizedException("You can only delete your own message.");

        msg.delete();
        return Map.of("message", "Message deleted successfully.");
    }

    // Helper Methods
    private ChatRoom findGroupByName(String groupName) {
        if (groupName == null) return null;
        for (ChatRoom chat : chats.values()) {
            if (chat.getChatType().equals("group") && chat.getChatName().equalsIgnoreCase(groupName.trim())) return chat;
        }
        return null;
    }

    private String getUserName(int userId) {
        User u = userDb.findById(userId);
        return (u != null) ? u.getName() : "Unknown";
    }

    private Map<String, Object> messageToMap(Message msg) {
        return Map.of(
                "message_id", msg.getMsgId(),
                "sender_id", msg.getSenderId(),
                "sender_name", getUserName(msg.getSenderId()),
                "content", msg.getContent(),
                "timestamp", msg.getTimestamp(),
                "is_deleted", msg.isDeleted()
        );
    }

    private Map<String, Object> chatToMap(ChatRoom chat) {
        List<String> memberNames = new ArrayList<>();
        for (int id : chat.getMembers()) {
            memberNames.add(getUserName(id));
        }
        return Map.of(
                "chat_id", chat.getChatId(),
                "chat_name", chat.getChatName(),
                "chat_type", chat.getChatType(),
                "members", chat.getMembers(),
                "member_names", memberNames
        );
    }
}
