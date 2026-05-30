package com.example.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EditMsgData {
    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("new_content")
    private String newContent;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }
}
