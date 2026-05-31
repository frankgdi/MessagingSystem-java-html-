package com.example.messaging.controller;

import com.example.messaging.model.*;
import com.example.messaging.dto.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    // 這裡保留你原本的聊天室邏輯
    // 注意：這裡已經沒有 /login 的 POST 請求了！

    @GetMapping("/friends/{userId}")
    public List<User> getFriends(@PathVariable int userId) {
        // 實作你的邏輯
        return new ArrayList<>(); 
    }

    @PostMapping("/friends/add")
    public Map<String, String> addFriend(@RequestBody AddFriendData data) {
        return Map.of("message", "Friend added successfully");
    }

    @GetMapping("/chats")
    public List<ChatRoom> getChats(@RequestParam("user_id") int userId) {
        return new ArrayList<>();
    }

    @GetMapping("/chats/{chatId}/messages")
    public List<Message> getMessages(@PathVariable String chatId) {
        return new ArrayList<>();
    }

    @PostMapping("/chats/{chatId}/messages")
    public void sendMessage(@PathVariable String chatId, @RequestBody SendMsgData data) {
        // 實作送訊息邏輯
    }
    
    // 其他編輯、刪除訊息的 API...
}
