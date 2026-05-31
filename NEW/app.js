let currentUser = null;
let currentChat = null;
let refreshTimer = null;

function showMessage(elementId, text, isError = false) {
    const element = document.getElementById(elementId);
    element.innerText = text;
    element.className = isError ? "error" : "success";
}

async function login() {
    const nameInput = document.getElementById("loginNameInput");
    const passInput = document.getElementById("loginPasswordInput"); // 新增：抓取密碼輸入框
    const name = nameInput.value.trim();
    const password = passInput.value.trim(); // 新增：取得密碼

    if (name === "" || password === "") {
        showMessage("loginMessage", "Please enter both name and password.", true);
        return;
    }

    try {
        // 修改：呼叫 apiLogin 時傳入密碼
        const data = await apiLogin(name, password);

        // 登入成功處理
        currentUser = data.user;
        // 如果後端回傳 token，可以存起來 (選用)
        if (data.token) localStorage.setItem("jwtToken", data.token);

        document.getElementById("loginPage").style.display = "none";
        document.getElementById("mainPage").style.display = "flex";
        document.getElementById("currentUserName").innerText = name;

        await loadFriends();
        await loadChats();

    } catch (error) {
        showMessage("loginMessage", error.message, true);
        console.log(error);
    }
}

// --- 以下其他功能保持不變 ---

async function loadFriends() {
    if (currentUser === null) return;
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
            div.onclick = function() { createPrivateChat(friend.user_id); };
            friendList.appendChild(div);
        });
    } catch (error) {
        alert(error.message);
        console.log(error);
    }
}

async function addFriend() {
    if (currentUser === null) { alert("Please log in first."); return; }
    const friendNameInput = document.getElementById("friendNameInput");
    const friendName = friendNameInput.value.trim();
    if (friendName === "") { showMessage("friendMessage", "Please enter your friend's name.", true); return; }
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

async function createGroup() {
    if (currentUser === null) { alert("Please log in first."); return; }
    const groupNameInput = document.getElementById("createGroupInput");
    const groupName = groupNameInput.value.trim();
    if (groupName === "") { showMessage("createGroupMessage", "Please enter a group name.", true); return; }
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

async function joinGroup() {
    if (currentUser === null) { alert("Please log in first."); return; }
    const groupNameInput = document.getElementById("joinGroupInput");
    const groupName = groupNameInput.value.trim();
    if (groupName === "") { showMessage("joinGroupMessage", "Please enter a group name.", true); return; }
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

async function loadChats() {
    if (currentUser === null) return;
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
            div.className = "item" + (currentChat && currentChat.chat_id === chat.chat_id ? " active" : "");
            div.innerText = chat.chat_name + " (" + chat.chat_type + ")";
            div.onclick = function() { openChat(chat); };
            chatList.appendChild(div);
        });
    } catch (error) {
        alert(error.message);
        console.log(error);
    }
}

async function openChat(chat) {
    currentChat = chat;
    document.getElementById("chatTitle").innerText = chat.chat_name;
    document.getElementById("chatInfo").innerText = "Type: " + chat.chat_type + "| Members: " + chat.member_names.join(", ");
    await loadMessages();
    await loadChats();
    if (refreshTimer !== null) clearInterval(refreshTimer);
    refreshTimer = setInterval(loadMessages, 2000);
}

async function loadMessages() {
    if (currentChat === null) return;
    try {
        const messages = await apiGetMessages(currentChat.chat_id);
        const chatArea = document.getElementById("chatArea");
        chatArea.innerHTML = "";
        if (messages.length === 0) { chatArea.innerHTML = "<p class='small-text'>No messages yet.</p>"; return; }
        messages.forEach(function(message) {
            const div = document.createElement("div");
            div.className = "message" + (message.sender_id === currentUser.userId ? " my-message" : "");

            const nameDiv = document.createElement("div");
            nameDiv.className = "message-name";
            nameDiv.innerText = message.sender_name;

            const contentDiv = document.createElement("div");
            contentDiv.className = "message-content";
            contentDiv.innerText = message.is_deleted ? "This message has been deleted." : message.content;
            if (message.is_deleted) contentDiv.style.color = "#888";

            div.appendChild(nameDiv);
            div.appendChild(contentDiv);

            if (message.sender_id === currentUser.userId && !message.is_deleted) {
                const actionDiv = document.createElement("div");
                actionDiv.className = "message-actions";
                const editBtn = document.createElement("button");
                editBtn.innerText = "Edit";
                editBtn.onclick = function() { editMessage(message.message_id, message.content); };
                const delBtn = document.createElement("button");
                delBtn.innerText = "Delete";
                delBtn.onclick = function() { deleteMessage(message.message_id); };
                actionDiv.appendChild(editBtn); actionDiv.appendChild(delBtn);
                div.appendChild(actionDiv);
            }
            chatArea.appendChild(div);
        });
        chatArea.scrollTop = chatArea.scrollHeight;
    } catch (error) { console.log(error.message); }
}

async function sendMessage() {
    if (currentChat === null) { alert("Please select a chat first."); return; }
    const messageInput = document.getElementById("messageInput");
    const content = messageInput.value.trim();
    if (content === "") return;
    try {
        await apiSendMessage(currentChat.chat_id, currentUser.userId, content);
        messageInput.value = "";
        await loadMessages();
    } catch (error) { alert(error.message); console.log(error); }
}

async function editMessage(messageId, oldContent) {
    const newContent = prompt("Enter the new message content:", oldContent);
    if (newContent === null || newContent.trim() === "") return;
    try {
        await apiEditMessage(currentChat.chat_id, messageId, currentUser.userId, newContent.trim());
        await loadMessages();
    } catch (error) { alert(error.message); console.log(error); }
}

async function deleteMessage(messageId) {
    const ok = confirm("Are you sure you want to delete this message?");
    if (!ok) return;
    try {
        await apiDeleteMessage(currentChat.chat_id, messageId, currentUser.userId);
        await loadMessages();
    } catch (error) { alert(error.message); console.log(error); }
}

document.getElementById("messageInput").addEventListener("keydown", function(e) { if(e.key === "Enter") sendMessage(); });
document.getElementById("loginNameInput").addEventListener("keydown", function(e) { if(e.key === "Enter") login(); });
