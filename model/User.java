package model;

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
            this.friends.add(friendId);
        }
    }

    // Getters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public List<Integer> getFriends() { return friends; }
}
