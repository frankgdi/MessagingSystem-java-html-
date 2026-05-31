package com.example.messaging.dto;

public class LoginData {
    private String username;
    private String password;

    // 【關鍵】補上這個無參數建構子，這能解決 500 Error
    public LoginData() {}

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
