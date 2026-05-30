package com.example.messaging.model;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private int chatId;
    private String chatName;
    private String chatType;
    private List<Integer> members;
    private List<Message> messages;

    public ChatRoom(int chatId, String chatName, String chatType, List<Integer> members) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.chatType = chatType;
        this.members = new ArrayList<>(members);
        this.messages = new ArrayList<>();
    }

    public void addMember(int userId) {
        if (!this.members.contains(userId)) {
            this.members.add(userId);
        }
    }

    public Message addMessage(int senderId, String text, int newMsgId) {
        if (!this.members.contains(senderId)) {
            throw new SecurityException("User is not a member of this chat.");
        }
        Message newMsg = new Message(newMsgId, senderId, text);
        this.messages.add(newMsg);
        return newMsg;
    }

    public Message findMessage(int msgId) {
        for (Message msg : this.messages) {
            if (msg.getMsgId() == msgId) {
                return msg;
            }
        }
        return null;
    }

    // Getters
    public int getChatId() { return chatId; }
    public String getChatName() { return chatName; }
    public String getChatType() { return chatType; }
    public List<Integer> getMembers() { return members; }
    public List<Message> getMessages() { return messages; }
}
