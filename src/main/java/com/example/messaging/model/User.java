package com.example.messaging.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String name;
    private String password; // 新增這行：符合登入驗證需求
    private List<Integer> friends;

    // 建議補上預設建構子，這對一些 Spring Boot 的轉換器很有幫助
    public User() {
        this.friends = new ArrayList<>();
    }

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
        this.friends = new ArrayList<>();
    }
    
    // 你的登入驗證會用到這個建構子
    public User(int userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.friends = new ArrayList<>();
    }

    public void addFriend(int friendId) {
        if (!this.friends.contains(friendId)) {
            this.friends.add(friendId);
        }
    }

    // Getters and Setters (全部補齊)
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Integer> getFriends() { return friends; }
    public void setFriends(List<Integer> friends) { this.friends = friends; }
}
