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

```

## 📁 前端專案結構 (Frontend Structure)

```text

frontend/ (或專案根目錄)
│
├── index.html            # 登入與主控制台畫面 (HTML 骨架)
│
├── css/                  # 負責美化介面的衣服
│   └── style.css         # 聊天室排版、對話泡泡、按鈕顏色
│
└── js/                   # 負責串接 Java API 的大腦 (JavaScript)
    ├── api.js            # 專門用 fetch() 呼叫 Java 控制器的函式 (如 login, sendMsg)
    └── app.js            # 處理按鈕點擊事件、動態更新 HTML 顯示訊息的邏輯

```

# Messaging System (Java + HTML) - 小組報告說明書

```text

這是一個使用 Java Spring Boot 作為後端 API 伺服器，並結合 HTML/CSS/JS 作為前端介面的即時通訊系統。

## 📁 後端專案進度 (Backend Implementation)

- [ ] `src/main/java/com/example/messaging/`
    - [ ] `model/` — 核心物件模型 (Domain Models)
        - [x] `User.java`（使用者類別）
        - [x] `Message.java`（訊息類別）
        - [x] `ChatRoom.java`（聊天室類別）
    - [ ] `dto/` — 資料傳遞物件 (Data Transfer Objects)
        - [x] `LoginData.java`
        - [ ] `AddFriendData.java`
        - [ ] `PrivateChatData.java`
        - [ ] `CreateGroupData.java`
        - [ ] `JoinGroupData.java`
        - [x] `SendMsgData.java`
        - [ ] `EditMsgData.java`
    - [ ] `controller/` — API 路由與記憶體資料庫
        - [x] `ChatController.java` (包含基礎 API 路由與 Fake DB)
        - [ ] Exception Handling 異常處理機制

## 📁 前端專案進度 (Frontend Implementation)

- [ ] `frontend/` (或專案根目錄)
    - [x] `index.html` — 登入與主控制台畫面 (HTML 骨架)
    - [ ] `css/` — 負責美化介面的衣服
        - [x] `style.css`（聊天室排版、對話泡泡、按鈕顏色）
    - [ ] `js/` — 負責串接 Java API 的大腦 (JavaScript)
        - [ ] `api.js`（專門用 fetch() 呼叫 Java 控制器的函式）
        - [x] `app.js`（處理按鈕點擊事件、動態更新 HTML 顯示訊息的邏輯）

## 🚀 簡報與書面進度 (Presentation & Assignment)
- [ ] 繪製 UML Class Diagram (類別圖)
- [ ] 製作簡報 PPT (6人黃金分工卡位)
- [ ] 撰寫書面報告文件 (Outline, Features, Methodology, Conclusion)

```
