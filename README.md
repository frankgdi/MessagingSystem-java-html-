# Messaging System (Java + HTML)

這是一個使用 Java Spring Boot 作為後端 API 伺服器，並結合 HTML/CSS/JS 作為前端介面的即時通訊系統。本次架構已升級為專業的三層架構 (Layered Architecture)。

## 📁 後端專案結構 (Backend Structure)

```text
src/main/java/com/example/messaging/
│
├── model/                 # 核心資料模型 (Domain Models)
│   ├── User.java          # 使用者類別 (含帳密欄位)
│   ├── Message.java       # 訊息類別
│   └── ChatRoom.java      # 聊天室類別
│
├── dto/                   # 資料傳遞物件 (Data Transfer Objects)
│   └── LoginData.java     # 登入用資料結構
│
├── controller/            # API 接客層 (請求處理)
│   ├── LoginController.java  # 專門處理登入驗證路由
│   └── ChatController.java   # 專門處理聊天與訊息邏輯
│
├── service/               # 商業邏輯層 (Business Logic)
│   ├── AuthService.java   # 帳號驗證與授權邏輯
│   └── TokenService.java  # Token 生成邏輯
│
└── repository/            # 資料存取層 (Data Access)
    └── UserDB.java        # 模擬資料庫操作

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
