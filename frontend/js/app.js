const apiUrl = "http://localhost:8080";

async function requestApi(url, method, body) {
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
        throw new Error(data.error || "Request failed");
    }

    return data;
}

function apiLogin(name) {
    return requestApi("/login", "POST", {
        name: name
    });
}

function apiGetFriends(userId) {
    return requestApi("/friends/" + userId, "GET", null);
}

function apiAddFriend(userId, friendName) {
    return requestApi("/friends/add", "POST", {
        userId: userId,
        friendName: friendName
    });
}

function apiCreatePrivateChat(userId, friendId) {
    return requestApi("/private-chat", "POST", {
        userId: userId,
        friendId: friendId
    });
}

function apiCreateGroup(userId, groupName) {
    return requestApi("/groups/create", "POST", {
        userId: userId,
        groupName: groupName
    });
}

function apiJoinGroup(userId, groupName) {
    return requestApi("/groups/join-by-name", "POST", {
        userId: userId,
        groupName: groupName
    });
}

function apiGetChats(userId) {
    return requestApi("/chats?user_id=" + userId, "GET", null);
}

function apiGetMessages(chatId) {
    return requestApi("/chats/" + chatId + "/messages", "GET", null);
}

function apiSendMessage(chatId, senderId, content) {
    return requestApi("/chats/" + chatId + "/messages", "POST", {
        senderId: senderId,
        content: content
    });
}

function apiEditMessage(chatId, messageId, userId, newContent) {
    return requestApi("/chats/" + chatId + "/messages/" + messageId, "PUT", {
        userId: userId,
        newContent: newContent
    });
}

function apiDeleteMessage(chatId, messageId, userId) {
    return requestApi(
        "/chats/" + chatId + "/messages/" + messageId + "?user_id=" + userId,
        "DELETE",
        null
    );
}
