package com.example.messaging.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TokenService {

    public String generateToken(int userId) {
        // 簡單模擬 JWT Token
        return "fake-jwt-" + UUID.randomUUID().toString() + "-user-" + userId;
    }
}
