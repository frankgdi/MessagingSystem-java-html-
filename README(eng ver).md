# Messaging System (Java + HTML) - Project Structure Documentation

This is a real-time messaging system built with Java Spring Boot as the backend API server and HTML/CSS/JS for the frontend user interface.

## 📁 Backend Project Structure

```text

src/main/java/com/example/messaging/
│
├── model/                 # Core domain models (Object logic)
│   ├── User.java          # User class
│   ├── Message.java       # Message class
│   └── ChatRoom.java      # ChatRoom class
│
├── dto/                   # Data Transfer Objects (Frontend request payloads)
│   ├── LoginData.java
│   ├── AddFriendData.java
│   ├── PrivateChatData.java
│   ├── CreateGroupData.java
│   ├── JoinGroupData.java
│   ├── SendMsgData.java
│   └── EditMsgData.java
│
└── controller/            # API routing controllers and in-memory database
    └── ChatController.java

```

## 📁 Backend Project Structure

```text

frontend/ (or project root directory)
│
├── index.html            # Login panel and main workspace layout (HTML skeleton)
│
├── css/                  # Styling and layout components
│   └── style.css         # Chatroom layout, speech bubbles, and button coloring
│
└── js/                   # Client-side core logic (JavaScript)
    ├── api.js            # Native fetch() methods calling the Java endpoints (e.g., login, sendMsg)
    └── app.js            # DOM event handlers and dynamic UI updates

```
