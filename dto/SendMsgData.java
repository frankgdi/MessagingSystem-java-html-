package com.example.messaging.dto;

public class SendMsgData {
    private int senderId;
    private String content;

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
