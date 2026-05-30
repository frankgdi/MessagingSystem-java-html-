# Messaging System (Java + HTML) - 小組報告說明書

這是一個使用 Java Spring Boot 作為後端 API 伺服器，並結合 HTML/CSS/JS 作為前端介面的即時通訊系統。

## 📁 後端專案結構 (Project Structure)

```text
src/main/java/com/example/messaging/
│
├── model/                 # 核心物件模型 (Domain Models)
│   ├── User.java          # 使用者類別
│   ├── Message.java       # 訊息類別
│   └── ChatRoom.java      # 聊天室類別
│
├── dto/                   # 接收前端欄位的資料傳遞物件 (Data Transfer Objects)
│   ├── LoginData.java
│   ├── AddFriendData.java
│   ├── PrivateChatData.java
│   ├── CreateGroupData.java
│   ├── JoinGroupData.java
│   ├── SendMsgData.java
│   └── EditMsgData.java
│
└── controller/            # API 路由控制器與記憶體資料庫
    └── ChatController.java
