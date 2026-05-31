package com.example.messaging.service;

import com.example.messaging.model.User;
import com.example.messaging.repository.UserDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserDB userDb;

    public boolean verifyCredentials(String username, String password) { /* ... */ }

    // 【新增這一段】
    public User createNewUser(String username, String password) {
        // 先建立 User 物件
        int newId = (int) (System.currentTimeMillis() % 10000); 
        User newUser = new User(newId, username);
        newUser.setPassword(password);
        // 存入 DB
        userDb.save(newUser);
        return newUser;
    }
}
