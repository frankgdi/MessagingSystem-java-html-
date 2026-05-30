package com.example.messaging.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Message {
    private int msgId;
    private int senderId;
    private String text;
    private LocalDateTime time;
    private boolean isDeleted;

    public Message(int msgId, int senderId, String text) {
        this.msgId = msgId;
        this.senderId = senderId;
        this.text = text;
        this.time = LocalDateTime.now();
        this.isDeleted = false;
    }

    public void edit(String newText) {
        if (this.isDeleted) {
            throw new IllegalArgumentException("Cannot edit a deleted message.");
        }
        this.text = newText;
    }

    public void delete() {
        this.isDeleted = true;
    }

    // 處理 Python 的 to_dict() 動態邏輯，Java 習慣直接在物件 getter 中做，或丟給 Controller 處理
    // 這裡我們建立一個專用 Method 來轉換成前端要的格式
    public String getContent() {
        return this.isDeleted ? "[This message has been deleted]" : this.text;
    }

    public String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.time.format(formatter);
    }

    // Getters
    public int getMsgId() { return msgId; }
    public int getSenderId() { return senderId; }
    public boolean isDeleted() { return isDeleted; }
}
