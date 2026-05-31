package com.example.messaging.repository;

import com.example.messaging.model.User;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDB {
    private Map<Integer, User> db = new HashMap<>();

    // 儲存或更新使用者
    public void save(User user) {
        db.put(user.getUserId(), user);
    }

    // 建構子：預設初始資料
    public UserDB() {
        User alice = new User(1, "Alice");
        alice.setPassword("1234");
        db.put(1, alice);

        User bob = new User(2, "Bob");
        bob.setPassword("5678");
        db.put(2, bob);
    }

    // 透過名稱查找使用者
    public User findUserByUsername(String username) {
        if (username == null) return null;
        for (User u : db.values()) {
            if (u.getName() != null && u.getName().equalsIgnoreCase(username.trim())) {
                return u;
            }
        }
        return null;
    }

    // 透過 ID 查找使用者
    public User findById(int id) {
        return db.get(id);
    }
}
