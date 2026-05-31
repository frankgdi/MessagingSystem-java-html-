const apiUrl = "http://localhost:8080";

// 底層通用請求大腦
async function requestApi(url, method = "GET", body = null) {
    const options = {
        method: method,
        headers: {
            "Content-Type": "application/json"
        }
    };

    if (body !== null) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(apiUrl + url, options);
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.error || data.message || "Request failed");
    }

    return data;
}

// 1. 登入
function apiLogin(name) {
    return requestApi("/login", "POST", {
        name: name
    });
}

// 2. 取得好友列表
function apiGetFriends(userId) {
    return requestApi("/friends/" + userId, "GET");
}

// 3. 加好友 (對齊 AddFriendData.java 欄位)
function apiAddFriend(userId, friendName) {
    return requestApi("/friends/add", "POST", {
        userId: userId,
        friendName: friendName
    });
}

// 4. 建立私人聊天室 (對齊 PrivateChatData.java 欄位)
function apiCreatePrivateChat(userId, friendId) {
    return requestApi("/private-chat", "POST", {
        userId: userId,
        friendId: friendId
    });
}

// 5. 建立群組 (對齊 CreateGroupData.java 欄位)
function apiCreateGroup(userId, groupName) {
    return requestApi("/groups/create", "POST", {
        userId: userId,
        groupName: groupName
    });
}

// 6. 加入群組 (對齊 JoinGroupData.java 欄位)
function apiJoinGroup(userId, groupName) {
    return requestApi("/groups/join-by-name", "POST", {
        userId: userId,
        groupName: groupName
    });
}

// 7. 取得聊天室列表
function apiGetChats(userId) {
    return requestApi("/chats?user_id=" + userId, "GET");
}

// 8. 取得聊天室內的訊息
function apiGetMessages(chatId) {
    return requestApi("/chats/" + chatId + "/messages", "GET");
}

// 9. 發送訊息 (對齊 SendMsgData.java 欄位)
function apiSendMessage(chatId, senderId, content) {
    return requestApi("/chats/" + chatId + "/messages", "POST", {
        senderId: senderId,
        content: content
    });
}

// 10. 編輯訊息 (對齊 EditMsgData.java 欄位)
function apiEditMessage(chatId, messageId, userId, newContent) {
    return requestApi("/chats/" + chatId + "/messages/" + messageId, "PUT", {
        userId: userId,
        newContent: newContent
    });
}

// 11. 刪除訊息
function apiDeleteMessage(chatId, messageId, userId) {
    return requestApi(
        "/chats/" + chatId + "/messages/" + messageId + "?user_id=" + userId,
        "DELETE"
    );
}
