const apiUrl = "http://localhost:8080";

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

    // 如果後端回傳 204 No Content，直接回傳 null
    if (response.status === 204) return null;

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.error || data.message || "Request failed");
    }

    return data;
}

// 登入：已更新為傳送 username 與 password
function apiLogin(username, password) {
    return requestApi("/login", "POST", {
        username: username,
        password: password
    });
}

// 取得好友列表
function apiGetFriends(userId) {
    return requestApi("/friends/" + userId, "GET");
}

// 加好友
function apiAddFriend(userId, friendName) {
    return requestApi("/friends/add", "POST", {
        user_id: userId,
        friend_name: friendName
    });
}

// 建立私人聊天室
function apiCreatePrivateChat(userId, friendId) {
    return requestApi("/private-chat", "POST", {
        user_id: userId,
        friend_id: friendId
    });
}

// 建立群組
function apiCreateGroup(userId, groupName) {
    return requestApi("/groups/create", "POST", {
        user_id: userId,
        group_name: groupName
    });
}

// 加入群組
function apiJoinGroup(userId, groupName) {
    return requestApi("/groups/join-by-name", "POST", {
        user_id: userId,
        group_name: groupName
    });
}

// 取得聊天室
function apiGetChats(userId) {
    return requestApi("/chats?user_id=" + userId, "GET");
}

// 取得訊息
function apiGetMessages(chatId) {
    return requestApi("/chats/" + chatId + "/messages", "GET");
}

// 送訊息
function apiSendMessage(chatId, senderId, content) {
    return requestApi("/chats/" + chatId + "/messages", "POST", {
        sender_id: senderId,
        content: content
    });
}

// 編輯訊息
function apiEditMessage(chatId, messageId, userId, newContent) {
    return requestApi("/chats/" + chatId + "/messages/" + messageId, "PUT", {
        user_id: userId,
        new_content: newContent
    });
}

// 刪除訊息
function apiDeleteMessage(chatId, messageId, userId) {
    return requestApi(
        "/chats/" + chatId + "/messages/" + messageId + "?user_id=" + userId,
        "DELETE"
    );
}
