package com.example.messaging.controller;

import com.example.messaging.dto.*;
import com.example.messaging.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, ChatRoom> chats = new HashMap<>();

    private int nextUserId = 5;
    private int nextMsgId = 1;
    private int nextChatId = 1;

    public ChatController() {
        users.put(1, new User(1, "Alice"));
        users.put(2, new User(2, "Bob"));
        users.put(3, new User(3, "Charlie"));
        users.put(4, new User(4, "David"));

        users.get(1).addFriend(2);
        users.get(2).addFriend(1);
    }

    private User findUserByName(String name) {
        if (name == null) return null;

        for (User u : users.values()) {
            if (u.getName().equalsIgnoreCase(name.trim())) {
                return u;
            }
        }
        return null;
    }

    private ChatRoom findGroupByName(String groupName) {
        if (groupName == null) return null;

        for (ChatRoom c : chats.values()) {
            if (c.getChatType().equals("group")
                    && c.getChatName().equalsIgnoreCase(groupName.trim())) {
                return c;
            }
        }
        return null;
    }

    private String getUserName(int userId) {
        if (!users.containsKey(userId)) {
            return "Unknown";
        }
        return users.get(userId).getName();
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

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "Chat Messaging System Backend is running.");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginData data) {
        if (data.getName() == null || data.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty.");
        }

        User oldUser = findUserByName(data.getName());

        if (oldUser != null) {
            return Map.of(
                    "message", "Login successfully.",
                    "user", oldUser
            );
        }

        int newId = nextUserId++;
        User newUser = new User(newId, data.getName().trim());
        users.put(newId, newUser);

        return Map.of(
                "message", "New user created and logged in.",
                "user", newUser
        );
    }

    @GetMapping("/friends/{userId}")
    public List<Map<String, Object>> getFriends(@PathVariable int userId) {
        if (!users.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        List<Map<String, Object>> friendList = new ArrayList<>();

        for (int friendId : users.get(userId).getFriends()) {
            if (users.containsKey(friendId)) {
                friendList.add(Map.of(
                        "user_id", users.get(friendId).getUserId(),
                        "name", users.get(friendId).getName()
                ));
            }
        }

        return friendList;
    }

    @PostMapping("/friends/add")
    public Map<String, Object> addFriend(@RequestBody AddFriendData data) {
        if (!users.containsKey(data.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        User friend = findUserByName(data.getFriendName());

        if (friend == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend not found.");
        }

        if (friend.getUserId() == data.getUserId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot add yourself.");
        }

        users.get(data.getUserId()).addFriend(friend.getUserId());
        friend.addFriend(data.getUserId());

        return Map.of(
                "message", "Friend added successfully.",
                "friend", friend
        );
    }

    @PostMapping("/private-chat")
    public Map<String, Object> createPrivateChat(@RequestBody PrivateChatData data) {
        if (!users.containsKey(data.getUserId()) || !users.containsKey(data.getFriendId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or friend not found.");
        }

        if (!users.get(data.getUserId()).getFriends().contains(data.getFriendId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must add this user as friend first.");
        }

        for (ChatRoom room : chats.values()) {
            if (room.getChatType().equals("private")
                    && room.getMembers().contains(data.getUserId())
                    && room.getMembers().contains(data.getFriendId())) {
                return Map.of(
                        "message", "Private chat already exists.",
                        "chat", chatToMap(room)
                );
            }
        }

        String chatName = users.get(data.getUserId()).getName()
                + " & "
                + users.get(data.getFriendId()).getName();

        ChatRoom newChat = new ChatRoom(
                nextChatId++,
                chatName,
                "private",
                List.of(data.getUserId(), data.getFriendId())
        );

        chats.put(newChat.getChatId(), newChat);

        return Map.of(
                "message", "Private chat created successfully.",
                "chat", chatToMap(newChat)
        );
    }

    @PostMapping("/groups/create")
    public Map<String, Object> createGroup(@RequestBody CreateGroupData data) {
        if (!users.containsKey(data.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        if (data.getGroupName() == null || data.getGroupName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group name cannot be empty.");
        }

        if (findGroupByName(data.getGroupName()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group already exists.");
        }

        ChatRoom group = new ChatRoom(
                nextChatId++,
                data.getGroupName().trim(),
                "group",
                List.of(data.getUserId())
        );

        chats.put(group.getChatId(), group);

        return Map.of(
                "message", "Group created successfully.",
                "chat", chatToMap(group)
        );
    }

    @PostMapping("/groups/join-by-name")
    public Map<String, Object> joinGroupByName(@RequestBody JoinGroupData data) {
        if (!users.containsKey(data.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        ChatRoom group = findGroupByName(data.getGroupName());

        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found.");
        }

        group.addMember(data.getUserId());

        return Map.of(
                "message", "Joined group successfully.",
                "chat", chatToMap(group)
        );
    }

    @GetMapping("/chats")
    public List<Map<String, Object>> getChats(@RequestParam("user_id") int userId) {
        if (!users.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (ChatRoom chat : chats.values()) {
            if (chat.getMembers().contains(userId)) {
                result.add(chatToMap(chat));
            }
        }

        return result;
    }

    @GetMapping("/chats/{chatId}/messages")
    public List<Map<String, Object>> getMessages(@PathVariable int chatId) {
        if (!chats.containsKey(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found.");
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Message msg : chats.get(chatId).getMessages()) {
            result.add(messageToMap(msg));
        }

        return result;
    }

    @PostMapping("/chats/{chatId}/messages")
    public Map<String, Object> sendMessage(@PathVariable int chatId, @RequestBody SendMsgData data) {
        if (!chats.containsKey(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found.");
        }

        try {
            ChatRoom chat = chats.get(chatId);
            Message newMsg = chat.addMessage(data.getSenderId(), data.getContent(), nextMsgId++);

            return Map.of(
                    "message", "Message sent successfully.",
                    "data", messageToMap(newMsg)
            );

        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/chats/{chatId}/messages/{msgId}")
    public Map<String, Object> editMessage(
            @PathVariable int chatId,
            @PathVariable int msgId,
            @RequestBody EditMsgData data
    ) {
        if (!chats.containsKey(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found.");
        }

        ChatRoom chat = chats.get(chatId);
        Message msg = chat.findMessage(msgId);

        if (msg == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found.");
        }

        if (msg.getSenderId() != data.getUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own message.");
        }

        msg.edit(data.getNewContent());

        return Map.of(
                "message", "Message edited successfully.",
                "data", messageToMap(msg)
        );
    }

    @DeleteMapping("/chats/{chatId}/messages/{msgId}")
    public Map<String, String> deleteMessage(
            @PathVariable int chatId,
            @PathVariable int msgId,
            @RequestParam("user_id") int userId
    ) {
        if (!chats.containsKey(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found.");
        }

        ChatRoom chat = chats.get(chatId);
        Message msg = chat.findMessage(msgId);

        if (msg == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found.");
        }

        if (msg.getSenderId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own message.");
        }

        msg.delete();

        return Map.of("message", "Message deleted successfully.");
    }
}
