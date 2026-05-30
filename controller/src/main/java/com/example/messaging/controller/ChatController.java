package com.example.messaging.controller;

import com.example.messaging.dto.*;
import com.example.messaging.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@CrossOrigin(origins = "*") // 允許 HTML 前端跨網域連線 (CORS)
public class ChatController {

    // ==========================================
    // Fake Database (記憶體資料庫)
    // ==========================================
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, ChatRoom> chats = new HashMap<>();

    private int nextUserId = 5;
    private int nextMsgId = 1;
    private int nextChatId = 1;

    // 建構子：初始化預設測試資料
    public ChatController() {
        users.put(1, new User(1, "Alice"));
        users.put(2, new User(2, "Bob"));
        users.put(3, new User(3, "Charlie"));
        users.put(4, new User(4, "David"));

        // 預設好友
        users.get(1).addFriend(2);
        users.get(2).addFriend(1);
    }

    // ==========================================
    // Helper Functions
    // ==========================================
    private User findUserByName(String name) {
        for (User u : users.values()) {
            if (u.getName().equalsIgnoreCase(name.trim())) return u;
        }
        return null;
    }

    private ChatRoom findGroupByName(String groupName) {
        for (ChatRoom c : chats.values()) {
            if (c.getChatType().equals("group") && c.getChatName().equalsIgnoreCase(groupName.trim())) {
                return c;
            }
        }
        return null;
    }

    // ==========================================
    // API 路由段落
    // ==========================================

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "Chat Messaging System Backend is running (Java Spring).");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginData data) {
        if (data.getName() == null || data.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty.");
        }

        User oldUser = findUserByName(data.getName());
        if (oldUser != null) {
            return Map.of("message", "Login successfully.", "user", oldUser);
        }

        int newId = nextUserId++;
        User newUser = new User(newId, data.getName().trim());
        users.put(newId, newUser);

        return Map.of("message", "New user created and logged in.", "user", newUser);
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

        return Map.of("message", "Friend added successfully.", "friend", friend);
    }

    @PostMapping("/private-chat")
    public Map<String, Object> createPrivateChat(@RequestBody PrivateChatData data) {
        if (!users.containsKey(data.getUserId()) || !users.containsKey(data.getFriendId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Friend not found.");
        }

        if (!users.get(data.getUserId()).getFriends().contains(data.getFriendId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must add this user as friend first.");
        }

        // 檢查私聊是否已存在
        for (ChatRoom room : chats.values()) {
            if (room.getChatType().equals("private") && 
                room.getMembers().contains(data.getUserId()) && 
                room.getMembers().contains(data.getFriendId())) {
                return Map.of("message", "Private chat already exists.", "chat", room);
            }
        }

        String chatName = users.get(data.getUserId()).getName() + " & " + users.get(data.getFriendId()).getName();
        ChatRoom newChat = new ChatRoom(nextChatId++, chatName, "private", List.of(data.getUserId(), data.getFriendId()));
        chats.put(newChat.getChatId(), newChat);

        return Map.of("message", "Private chat created successfully.", "chat", newChat);
    }

    @PostMapping("/chats/{chatId}/messages")
    public Map<String, Object> sendMessage(@PathVariable int chatId, @RequestBody SendMsgData data) {
        if (!chats.containsKey(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found.");
        }

        try {
            ChatRoom chat = chats.get(chatId);
            Message newMsg = chat.addMessage(data.getSenderId(), data.getContent(), nextMsgId++);
            
            // 包裝輸出格式
            Map<String, Object> msgMap = Map.of(
                "message_id", newMsg.getMsgId(),
                "sender_id", newMsg.getSenderId(),
                "sender_name", users.containsKey(newMsg.getSenderId()) ? users.get(newMsg.getSenderId()).getName() : "Unknown",
                "content", newMsg.getContent(),
                "timestamp", newMsg.getTimestamp(),
                "is_deleted", newMsg.isDeleted()
            );

            return Map.of("message", "Message sent successfully.", "data", msgMap);
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
    
    // 提示：剩餘的群組建立、編輯與刪除訊息，邏輯結構皆與上方完全相同，依照對應的 DTO 進行修改即可。
}
