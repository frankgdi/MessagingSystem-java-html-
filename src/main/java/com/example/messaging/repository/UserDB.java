package com.example.messaging.repository;

import com.example.messaging.model.User;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDB {
    private Map<Integer, User> db = new HashMap<>();

    public UserDB() {
        // 預設一筆測試帳號
        User alice = new User(1, "Alice");
        alice.setPassword("1234");
        db.put(1, alice);
    }

    public User findUserByUsername(String username) {
        for (User u : db.values()) {
            if (u.getName().equalsIgnoreCase(username.trim())) return u;
        }
        return null;
    }

    public User findById(int id) {
        return db.get(id);
    }
}
