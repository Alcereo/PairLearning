package ru.alcereo.pairlearning.Service;

import ru.alcereo.pairlearning.SocketChat.ChatSocketConnection;

import javax.websocket.Session;

public interface RoomFabric {

    ChatRoom newRoom(UserFront user, MessageHandler handler);

}
