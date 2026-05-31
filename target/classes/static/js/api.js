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
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.error || data.message || "Request failed");
    }

    return data;
}

// 登入
// LoginData 如果沒有 @JsonProperty，這裡維持 name 即可
function apiLogin(name) {
    return requestApi("/login", "POST", {
        name: name
    });
}

// 取得好友列表
function apiGetFriends(userId) {
    return requestApi("/friends/" + userId, "GET");
}

// 加好友
// 對應 AddFriendData.java:
// @JsonProperty("user_id")
// @JsonProperty("friend_name")
function apiAddFriend(userId, friendName) {
    return requestApi("/friends/add", "POST", {
        user_id: userId,
        friend_name: friendName
    });
}

// 建立私人聊天室
// 對應 PrivateChatData.java:
// @JsonProperty("user_id")
// @JsonProperty("friend_id")
function apiCreatePrivateChat(userId, friendId) {
    return requestApi("/private-chat", "POST", {
        user_id: userId,
        friend_id: friendId
    });
}

// 建立群組
// 對應 CreateGroupData.java:
// @JsonProperty("user_id")
// @JsonProperty("group_name")
function apiCreateGroup(userId, groupName) {
    return requestApi("/groups/create", "POST", {
        user_id: userId,
        group_name: groupName
    });
}

// 加入群組
// 對應 JoinGroupData.java:
// @JsonProperty("user_id")
// @JsonProperty("group_name")
function apiJoinGroup(userId, groupName) {
    return requestApi("/groups/join-by-name", "POST", {
        user_id: userId,
        group_name: groupName
    });
}

// 取得聊天室
// 這裡後端本來就是 @RequestParam("user_id")
// 所以網址仍然是 user_id
function apiGetChats(userId) {
    return requestApi("/chats?user_id=" + userId, "GET");
}

// 取得訊息
function apiGetMessages(chatId) {
    return requestApi("/chats/" + chatId + "/messages", "GET");
}

// 送訊息
// 對應 SendMsgData.java:
// @JsonProperty("sender_id")
// content
function apiSendMessage(chatId, senderId, content) {
    return requestApi("/chats/" + chatId + "/messages", "POST", {
        sender_id: senderId,
        content: content
    });
}

// 編輯訊息
// 對應 EditMsgData.java:
// @JsonProperty("user_id")
// @JsonProperty("new_content")
function apiEditMessage(chatId, messageId, userId, newContent) {
    return requestApi("/chats/" + chatId + "/messages/" + messageId, "PUT", {
        user_id: userId,
        new_content: newContent
    });
}

// 刪除訊息
// 這裡後端是 @RequestParam("user_id")
// 所以網址仍然是 user_id
function apiDeleteMessage(chatId, messageId, userId) {
    return requestApi(
        "/chats/" + chatId + "/messages/" + messageId + "?user_id=" + userId,
        "DELETE"
    );
}
