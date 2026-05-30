from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from datetime import datetime

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# =========================
# Class
# =========================

class User:
    def __init__(self, user_id, name):
        self.user_id = user_id
        self.name = name
        self.friends = []

    def add_friend(self, friend_id):
        if friend_id not in self.friends:
            self.friends.append(friend_id)

    def to_dict(self):
        return {
            "user_id": self.user_id,
            "name": self.name,
            "friends": self.friends
        }


class Message:
    def __init__(self, msg_id, sender_id, text):
        self.msg_id = msg_id
        self.sender_id = sender_id
        self.text = text
        self.time = datetime.now()
        self.is_deleted = False

    def edit(self, new_text):
        if self.is_deleted:
            raise ValueError("Cannot edit a deleted message.")

        self.text = new_text

    def delete(self):
        self.is_deleted = True

    def to_dict(self):
        sender_name = "Unknown"

        if self.sender_id in users:
            sender_name = users[self.sender_id].name

        show_text = self.text

        if self.is_deleted:
            show_text = "[This message has been deleted]"

        return {
            "message_id": self.msg_id,
            "sender_id": self.sender_id,
            "sender_name": sender_name,
            "content": show_text,
            "timestamp": self.time.strftime("%Y-%m-%d %H:%M:%S"),
            "is_deleted": self.is_deleted
        }


class ChatRoom:
    def __init__(self, chat_id, chat_name, chat_type, members):
        self.chat_id = chat_id
        self.chat_name = chat_name
        self.chat_type = chat_type
        self.members = members
        self.messages = []

    def add_member(self, user_id):
        if user_id not in self.members:
            self.members.append(user_id)

    def add_message(self, sender_id, text):
        if sender_id not in self.members:
            raise PermissionError("User is not a member of this chat.")

        new_msg = Message(
            msg_id=get_new_msg_id(),
            sender_id=sender_id,
            text=text
        )

        self.messages.append(new_msg)
        return new_msg

    def find_message(self, msg_id):
        for msg in self.messages:
            if msg.msg_id == msg_id:
                return msg

        return None

    def to_dict(self):
        name_list = []

        for user_id in self.members:
            if user_id in users:
                name_list.append(users[user_id].name)

        return {
            "chat_id": self.chat_id,
            "chat_name": self.chat_name,
            "chat_type": self.chat_type,
            "members": self.members,
            "member_names": name_list
        }


# =========================
# Request Body
# =========================

class LoginData(BaseModel):
    name: str


class AddFriendData(BaseModel):
    user_id: int
    friend_name: str


class PrivateChatData(BaseModel):
    user_id: int
    friend_id: int


class CreateGroupData(BaseModel):
    user_id: int
    group_name: str


class JoinGroupData(BaseModel):
    user_id: int
    group_name: str


class SendMsgData(BaseModel):
    sender_id: int
    content: str


class EditMsgData(BaseModel):
    user_id: int
    new_content: str


# =========================
# Fake Database
# =========================

users = {
    1: User(1, "Alice"),
    2: User(2, "Bob"),
    3: User(3, "Charlie"),
    4: User(4, "David")
}

# 預設好友，方便測試
users[1].add_friend(2)
users[2].add_friend(1)

# 沒有預設群組
# 群組要由使用者自己建立
chats = {}

next_user_id = 5
next_msg_id = 1
next_chat_id = 1


# =========================
# Helper Functions
# =========================

def get_new_user_id():
    global next_user_id

    new_id = next_user_id
    next_user_id += 1

    return new_id


def get_new_msg_id():
    global next_msg_id

    new_id = next_msg_id
    next_msg_id += 1

    return new_id


def get_new_chat_id():
    global next_chat_id

    new_id = next_chat_id
    next_chat_id += 1

    return new_id


def find_user(name):
    for one_user in users.values():
        if one_user.name.lower() == name.lower():
            return one_user

    return None


def find_group(group_name):
    for one_chat in chats.values():
        if one_chat.chat_type == "group":
            if one_chat.chat_name.lower() == group_name.lower():
                return one_chat

    return None


# =========================
# API
# =========================

@app.get("/")
def home():
    return {
        "message": "Chat Messaging System Backend is running."
    }


@app.post("/login")
def login(data: LoginData):
    name = data.name.strip()

    if name == "":
        raise HTTPException(status_code=400, detail="Name cannot be empty.")

    old_user = find_user(name)

    if old_user is not None:
        return {
            "message": "Login successfully.",
            "user": old_user.to_dict()
        }

    new_id = get_new_user_id()
    new_user = User(new_id, name)
    users[new_id] = new_user

    return {
        "message": "New user created and logged in.",
        "user": new_user.to_dict()
    }


@app.get("/friends/{user_id}")
def get_friends(user_id: int):
    if user_id not in users:
        raise HTTPException(status_code=404, detail="User not found.")

    me = users[user_id]
    answer = []

    for friend_id in me.friends:
        if friend_id in users:
            answer.append({
                "user_id": users[friend_id].user_id,
                "name": users[friend_id].name
            })

    return answer


@app.post("/friends/add")
def add_friend(data: AddFriendData):
    user_id = data.user_id
    friend_name = data.friend_name.strip()

    if user_id not in users:
        raise HTTPException(status_code=404, detail="User not found.")

    if friend_name == "":
        raise HTTPException(status_code=400, detail="Friend name cannot be empty.")

    friend = find_user(friend_name)

    if friend is None:
        raise HTTPException(status_code=404, detail="Friend not found.")

    if friend.user_id == user_id:
        raise HTTPException(status_code=400, detail="You cannot add yourself as friend.")

    me = users[user_id]

    me.add_friend(friend.user_id)
    friend.add_friend(user_id)

    return {
        "message": "Friend added successfully.",
        "friend": {
            "user_id": friend.user_id,
            "name": friend.name
        }
    }


@app.post("/private-chat")
def create_private_chat(data: PrivateChatData):
    user_id = data.user_id
    friend_id = data.friend_id

    if user_id not in users:
        raise HTTPException(status_code=404, detail="User not found.")

    if friend_id not in users:
        raise HTTPException(status_code=404, detail="Friend not found.")

    me = users[user_id]

    if friend_id not in me.friends:
        raise HTTPException(
            status_code=403,
            detail="You must add this user as friend first."
        )

    for one_chat in chats.values():
        if one_chat.chat_type == "private":
            if set(one_chat.members) == set([user_id, friend_id]):
                return {
                    "message": "Private chat already exists.",
                    "chat": one_chat.to_dict()
                }

    me_name = users[user_id].name
    friend_name = users[friend_id].name

    new_chat = ChatRoom(
        chat_id=get_new_chat_id(),
        chat_name=f"{me_name} & {friend_name}",
        chat_type="private",
        members=[user_id, friend_id]
    )

    chats[new_chat.chat_id] = new_chat

    return {
        "message": "Private chat created successfully.",
        "chat": new_chat.to_dict()
    }


@app.post("/groups/create")
def create_group(data: CreateGroupData):
    user_id = data.user_id
    group_name = data.group_name.strip()

    if user_id not in users:
        raise HTTPException(status_code=404, detail="User not found.")

    if group_name == "":
        raise HTTPException(status_code=400, detail="Group name cannot be empty.")

    old_group = find_group(group_name)

    if old_group is not None:
        raise HTTPException(
            status_code=400,
            detail="Group name already exists. Please use another name."
        )

    new_group = ChatRoom(
        chat_id=get_new_chat_id(),
        chat_name=group_name,
        chat_type="group",
        members=[user_id]
    )

    chats[new_group.chat_id] = new_group

    return {
        "message": "Group created successfully.",
        "chat": new_group.to_dict()
    }


@app.post("/groups/join-by-name")
def join_group_by_name(data: JoinGroupData):
    user_id = data.user_id
    group_name = data.group_name.strip()

    if user_id not in users:
        raise HTTPException(status_code=404, detail="User not found.")

    if group_name == "":
        raise HTTPException(status_code=400, detail="Group name cannot be empty.")

    group = find_group(group_name)

    if group is None:
        raise HTTPException(status_code=404, detail="Group not found.")

    group.add_member(user_id)

    return {
        "message": "Joined group successfully.",
        "chat": group.to_dict()
    }


@app.get("/chats")
def get_chats(user_id: int):
    if user_id not in users:
        raise HTTPException(status_code=404, detail="User not found.")

    answer = []

    for one_chat in chats.values():
        if user_id in one_chat.members:
            answer.append(one_chat.to_dict())

    return answer


@app.get("/chats/{chat_id}/messages")
def get_messages(chat_id: int):
    if chat_id not in chats:
        raise HTTPException(status_code=404, detail="Chat room not found.")

    chat = chats[chat_id]
    answer = []

    for msg in chat.messages:
        answer.append(msg.to_dict())

    return answer


@app.post("/chats/{chat_id}/messages")
def send_message(chat_id: int, data: SendMsgData):
    if chat_id not in chats:
        raise HTTPException(status_code=404, detail="Chat room not found.")

    if data.sender_id not in users:
        raise HTTPException(status_code=404, detail="User not found.")

    chat = chats[chat_id]

    try:
        new_msg = chat.add_message(data.sender_id, data.content)
    except PermissionError as error:
        raise HTTPException(status_code=403, detail=str(error))

    return {
        "message": "Message sent successfully.",
        "data": new_msg.to_dict()
    }


@app.put("/chats/{chat_id}/messages/{msg_id}")
def edit_message(chat_id: int, msg_id: int, data: EditMsgData):
    if chat_id not in chats:
        raise HTTPException(status_code=404, detail="Chat room not found.")

    if data.user_id not in users:
        raise HTTPException(status_code=404, detail="User not found.")

    chat = chats[chat_id]
    msg = chat.find_message(msg_id)

    if msg is None:
        raise HTTPException(status_code=404, detail="Message not found.")

    if msg.sender_id != data.user_id:
        raise HTTPException(
            status_code=403,
            detail="You can only edit your own messages."
        )

    try:
        msg.edit(data.new_content)
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))

    return {
        "message": "Message edited successfully.",
        "data": msg.to_dict()
    }


@app.delete("/chats/{chat_id}/messages/{msg_id}")
def delete_message(chat_id: int, msg_id: int, user_id: int):
    if chat_id not in chats:
        raise HTTPException(status_code=404, detail="Chat room not found.")

    if user_id not in users:
        raise HTTPException(status_code=404, detail="User not found.")

    chat = chats[chat_id]
    msg = chat.find_message(msg_id)

    if msg is None:
        raise HTTPException(status_code=404, detail="Message not found.")

    if msg.sender_id != user_id:
        raise HTTPException(
            status_code=403,
            detail="You can only delete your own messages."
        )

    msg.delete()

    return {
        "message": "Message deleted successfully.",
        "data": msg.to_dict()
    }