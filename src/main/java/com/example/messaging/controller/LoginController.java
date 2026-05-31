package com.example.messaging.controller;

import com.example.messaging.dto.LoginData;
import com.example.messaging.service.AuthService;
import com.example.messaging.service.TokenService;
import com.example.messaging.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginData data) {
        // 1. 嘗試根據帳號名取得用戶
        User user = authService.getUserByUsername(data.getUsername());

        // 2. 如果用戶不存在，自動執行註冊邏輯
        if (user == null) {
            user = authService.createNewUser(data.getUsername(), data.getPassword());
            String token = tokenService.generateToken(user.getUserId());
            return ResponseEntity.ok(Map.of(
                "message", "User created and logged in.", 
                "user", user, 
                "token", token
            ));
        }

        // 3. 如果用戶已存在，驗證密碼
        if (!user.getPassword().equals(data.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Wrong password"));
        }

        // 4. 登入成功
        String token = tokenService.generateToken(user.getUserId());
        return ResponseEntity.ok(Map.of(
            "message", "Login successfully.", 
            "user", user, 
            "token", token
        ));
    }
}
