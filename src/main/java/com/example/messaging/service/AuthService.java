package com.example.messaging.service;

import com.example.messaging.model.User;
import com.example.messaging.repository.UserDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserDB userDb;

    public boolean verifyCredentials(String username, String password) {
        User user = userDb.findUserByUsername(username);
        if (user == null) return false;
        
        return user.getPassword().equals(password);
    }

    public User getUserByUsername(String username) {
        return userDb.findUserByUsername(username);
    }
}
