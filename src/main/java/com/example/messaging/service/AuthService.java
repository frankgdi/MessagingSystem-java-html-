package com.example.messaging.service;

import com.example.messaging.model.User;
import com.example.messaging.repository.UserDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserDB userDb;

    // 驗證帳號密碼
    public boolean verifyCredentials(String username, String password) {
        User user = userDb.findUserByUsername(username);
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(password);
    }

    // 根據名稱取得 User
    public User getUserByUsername(String username) {
        return userDb.findUserByUsername(username);
    }

    // 建立新用戶
    public User createNewUser(String username, String password) {
        // 簡單模擬建立新用戶 (ID 使用時間戳記避免重複)
        int newId = (int) (System.currentTimeMillis() % 10000); 
        User newUser = new User(newId, username);
        newUser.setPassword(password);
        
        // 存入 DB
        userDb.save(newUser);
        return newUser;
    }
}
