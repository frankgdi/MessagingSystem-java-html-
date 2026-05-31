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
        // 1. 驗證帳密
        boolean isAuth = authService.verifyCredentials(data.getUsername(), data.getPassword());
        
        if (!isAuth) {
            return ResponseEntity.status(401).body(Map.of("error", "Login failed: Invalid credentials"));
        }

        // 2. 登入成功，取得 User 與 Token
        User user = authService.getUserByUsername(data.getUsername());
        String token = tokenService.generateToken(user.getUserId());

        return ResponseEntity.ok(Map.of(
            "message", "Login successfully.",
            "user", user,
            "token", token
        ));
    }
}
