package com.example.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinGroupData {
    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("group_name")
    private String groupName;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
