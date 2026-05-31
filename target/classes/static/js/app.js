let currentUser = null;
let currentChat = null;
let refreshTimer = null;

// 顯示提示訊息 (防呆用)
function showMessage(elementId, text, isError = false) {
    const element = document.getElementById(elementId);
    element.innerText = text;

    if (isError) {
        element.className = "error";
    } else {
        element.className = "success";
    }
}

// 登入功能
async function login() {
    const nameInput = document.getElementById("loginNameInput");
    const name = nameInput.value.trim();

    if (name === "") {
        showMessage("loginMessage", "Please enter your name.", true);
        return;
    }

    try {
        const data = await apiLogin(name);

        currentUser = data.user;

        document.getElementById("loginPage").style.display = "none";
        document.getElementById("mainPage").style.display = "flex";
        document.getElementById("currentUserName").innerText = currentUser.name;

        await loadFriends();
        await loadChats();

    } catch (error) {
        showMessage("loginMessage", error.message, true);
        console.log(error);
    }
}

// 載入好友列表
async function loadFriends() {
    if (currentUser === null) {
        return;
    }

    try {
        const friends = await apiGetFriends(currentUser.userId);

        const friendList = document.getElementById("friendList");
        friendList.innerHTML = "";

        if (friends.length === 0) {
            friendList.innerHTML = "<p class='small-text'>No friends yet.</p>";
            return;
        }

        friends.forEach(function(friend) {
            const div = document.createElement("div");
            div.className = "item";
            div.innerText = friend.name;

            div.onclick = function() {
                // 修正：後端傳回的是 userId，不是 user_id
                createPrivateChat(friend.userId);
            };

            friendList.appendChild(div);
        });

    } catch (error) {
        alert(error.message);
        console.log(error);
    }
}

// 新增好友
async function addFriend() {
    if (currentUser === null) {
        alert("Please log in first.");
        return;
    }

    const friendNameInput = document.getElementById("friendNameInput");
    const friendName = friendNameInput.value.trim();

    if (friendName === "") {
        showMessage("friendMessage", "Please enter your friend's name.", true);
        return;
    }

    try {
        const data = await apiAddFriend(currentUser.userId, friendName);

        showMessage("friendMessage", data.message, false);
        friendNameInput.value = "";

        await loadFriends();

    } catch (error) {
        showMessage("friendMessage", error.message, true);
        console.log(error);
    }
}

// 建立私人聊天室
async function createPrivateChat(friendId) {
    try {
        const data = await apiCreatePrivateChat(currentUser.userId, friendId);

        currentChat = data.chat;

        await loadChats();
        await openChat(currentChat);

    } catch (error) {
        alert(error.message);
        console.log(error);
    }
}

// 建立群組
async function createGroup() {
    if (currentUser === null) {
        alert("Please log in first.");
        return;
    }

    const groupNameInput = document.getElementById("createGroupInput");
    const groupName = groupNameInput.value.trim();

    if (groupName === "") {
        showMessage("createGroupMessage", "Please enter a group name.", true);
        return;
    }

    try {
        const data = await apiCreateGroup(currentUser.userId, groupName);

        showMessage("createGroupMessage", data.message, false);
        groupNameInput.value = "";

        await loadChats();
        await openChat(data.chat);

    } catch (error) {
        showMessage("createGroupMessage", error.message, true);
        console.log(error);
    }
}

// 加入群組
async function joinGroup() {
    if (currentUser === null) {
        alert("Please log in first.");
        return;
    }

    const groupNameInput = document.getElementById("joinGroupInput");
    const groupName = groupNameInput.value.trim();

    if (groupName === "") {
        showMessage("joinGroupMessage", "Please enter a group name.", true);
        return;
    }

    try {
        const data = await apiJoinGroup(currentUser.userId, groupName);

        showMessage("joinGroupMessage", data.message, false);
        groupNameInput.value = "";

        await loadChats();
        await openChat(data.chat);

    } catch (error) {
        showMessage("joinGroupMessage", error.message, true);
        console.log(error);
    }
}

// 載入聊天室列表
async function loadChats() {
    if (currentUser === null) {
        return;
    }

    try {
        const chats = await apiGetChats(currentUser.userId);

        const chatList = document.getElementById("chatList");
        chatList.innerHTML = "";

        if (chats.length === 0) {
            chatList.innerHTML = "<p class='small-text'>No chat yet.</p>";
            return;
        }

        chats.forEach(function(chat) {
            const div = document.createElement("div");
            div.className = "item";

            // 修正：後端回傳的是 chatId，不是 chat_id
            if (currentChat !== null && currentChat.chatId === chat.chatId) {
                div.className = "item active";
            }

            // 修正：後端回傳的是 chatName 與 chatType
            div.innerText = chat.chatName + " (" + chat.chatType + ")";

            div.onclick = function() {
                openChat(chat);
            };

            chatList.appendChild(div);
        });

    } catch (error) {
        alert(error.message);
        console.log(error);
    }
}

// 點擊開啟聊天室
async function openChat(chat) {
    currentChat = chat;

    // 修正：後端回傳的是 chatName, chatType, memberNames
    document.getElementById("chatTitle").innerText = chat.chatName;
    document.getElementById("chatInfo").innerText =
        "Type: " + chat.chatType + " | Members: " + chat.memberNames.join(", ");

    await loadMessages();
    await loadChats();

    if (refreshTimer !== null) {
        clearInterval(refreshTimer);
    }

    refreshTimer = setInterval(loadMessages, 2000);
}

// 載入聊天室內的訊息 (每2秒輪詢一次)
async function loadMessages() {
    if (currentChat === null) {
        return;
    }

    try {
        // 修正：後端回傳的是 chatId
        const messages = await apiGetMessages(currentChat.chatId);

        const chatArea = document.getElementById("chatArea");
        chatArea.innerHTML = "";

        if (messages.length === 0) {
            chatArea.innerHTML = "<p class='small-text'>No messages yet.</p>";
            return;
        }

        messages.forEach(function(message) {
            const div = document.createElement("div");

            // 修正：後端回傳的是 senderId
            if (message.senderId === currentUser.userId) {
                div.className = "message my-message";
            } else {
                div.className = "message";
            }

            const nameDiv = document.createElement("div");
            nameDiv.className = "message-name";
            // 修正：後端回傳的是 senderName
            nameDiv.innerText = message.senderName;

            const contentDiv = document.createElement("div");
            contentDiv.className = "message-content";

            // 修正：後端回傳的是 deleted
            if (message.deleted) {
                contentDiv.innerText = "This message has been deleted.";
                contentDiv.style.color = "#888";
            } else {
                contentDiv.innerText = message.content;
            }

            div.appendChild(nameDiv);
            div.appendChild(contentDiv);

            // 修正：防呆判斷與編輯/刪除綁定欄位
            if (message.senderId === currentUser.userId && !message.deleted) {
                const actionDiv = document.createElement("div");
                actionDiv.className = "message-actions";

                const editButton = document.createElement("button");
                editButton.innerText = "Edit";
                editButton.onclick = function() {
                    // 修正：後端回傳的是 messageId
                    editMessage(message.messageId, message.content);
                };

                const deleteButton = document.createElement("button");
                deleteButton.innerText = "Delete";
                deleteButton.onclick = function() {
                    // 修正：後端回傳的是 messageId
                    deleteMessage(message.messageId);
                };

                actionDiv.appendChild(editButton);
                actionDiv.appendChild(deleteButton);
                div.appendChild(actionDiv);
            }

            chatArea.appendChild(div);
        });

        chatArea.scrollTop = chatArea.scrollHeight;

    } catch (error) {
        console.log(error.message);
    }
}

// 發送訊息
async function sendMessage() {
    if (currentChat === null) {
        alert("Please select a chat first.");
        return;
    }

    const messageInput = document.getElementById("messageInput");
    const content = messageInput.value.trim();

    if (content === "") {
        return;
    }

    try {
        await apiSendMessage(
            currentChat.chatId, // 修正
            currentUser.userId,
            content
        );

        messageInput.value = "";
        await loadMessages();

    } catch (error) {
        alert(error.message);
        console.log(error);
    }
}

// 編輯訊息
async function editMessage(messageId, oldContent) {
    const newContent = prompt("Enter the new message content:", oldContent);

    if (newContent === null || newContent.trim() === "") {
        return;
    }

    try {
        await apiEditMessage(
            currentChat.chatId, // 修正
            messageId,
            currentUser.userId,
            newContent.trim()
        );

        await loadMessages();

    } catch (error) {
        alert(error.message);
        console.log(error);
    }
}

// 刪除訊息
async function deleteMessage(messageId) {
    const ok = confirm("Are you sure you want to delete this message?");

    if (!ok) {
        return;
    }

    try {
        await apiDeleteMessage(
            currentChat.chatId, // 修正
            messageId,
            currentUser.userId
        );

        await loadMessages();

    } catch (error) {
        alert(error.message);
        console.log(error);
    }
}

// 鍵盤監聽事件 (按 Enter 直接送出)
document.getElementById("messageInput").addEventListener("keydown", function(event) {
    if (event.key === "Enter") {
        sendMessage();
    }
});

document.getElementById("loginNameInput").addEventListener("keydown", function(event) {
    if (event.key === "Enter") {
        login();
    }
});
