package com.example.messaging.controller;

import com.example.messaging.model.*;
import com.example.messaging.dto.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    // 取得好友列表 - 修復參數名稱
    @GetMapping("/friends/{userId}")
    public List<User> getFriends(@PathVariable("userId") int userId) {
        // 這裡回傳你的邏輯
        return new ArrayList<>(); 
    }

    // 加好友
    @PostMapping("/friends/add")
    public Map<String, String> addFriend(@RequestBody AddFriendData data) {
        return Map.of("message", "Friend added successfully");
    }

    // 取得聊天室列表 - 修復參數名稱
    @GetMapping("/chats")
    public List<ChatRoom> getChats(@RequestParam("user_id") int userId) {
        return new ArrayList<>();
    }

    // 取得訊息 - 修復參數名稱
    @GetMapping("/chats/{chatId}/messages")
    public List<Message> getMessages(@PathVariable("chatId") String chatId) {
        return new ArrayList<>();
    }

    // 送訊息 - 修復參數名稱
    @PostMapping("/chats/{chatId}/messages")
    public void sendMessage(@PathVariable("chatId") String chatId, @RequestBody SendMsgData data) {
        // 處理發送訊息邏輯
    }

    // 編輯訊息 - 修復參數名稱
    @PutMapping("/chats/{chatId}/messages/{messageId}")
    public void editMessage(@PathVariable("chatId") String chatId, 
                            @PathVariable("messageId") String messageId, 
                            @RequestBody EditMsgData data) {
        // 處理編輯訊息邏輯
    }

    // 刪除訊息 - 修復參數名稱
    @DeleteMapping("/chats/{chatId}/messages/{messageId}")
    public void deleteMessage(@PathVariable("chatId") String chatId, 
                              @PathVariable("messageId") String messageId, 
                              @RequestParam("user_id") int userId) {
        // 處理刪除訊息邏輯
    }
}
