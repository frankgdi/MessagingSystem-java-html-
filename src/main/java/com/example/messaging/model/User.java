package com.example.messaging.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String name;
    private List<Integer> friends;

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
        this.friends = new ArrayList<>();
    }

    public void addFriend(int friendId) {
        if (!this.friends.contains(friendId)) {
            //this.friends.append(friendId); // 在 Java 中建議寫成封裝方法
            this.friends.add(friendId);
        }
    }

    // Getters and Setters (Java 需要這些來轉換為 JSON)
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public List<Integer> getFriends() { return friends; }
}
