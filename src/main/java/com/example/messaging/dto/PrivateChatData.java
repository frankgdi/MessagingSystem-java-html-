package com.example.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivateChatData {
    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("friend_id")
    private int friendId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }
}
