package com.example.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddFriendData {
    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("friend_name")
    private String friendName;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
