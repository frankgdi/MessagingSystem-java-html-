package com.example.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendMsgData {
    @JsonProperty("sender_id")
    private int senderId;

    private String content;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}