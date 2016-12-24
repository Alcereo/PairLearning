package ru.alcereo.pairlearning.Service;

import ru.alcereo.pairlearning.SocketChat.ChatSocketConnection;

import javax.websocket.Session;

public class RoomGroupedFabric implements RoomFabric {

    @Override
    public ChatRoom newRoom(UserFront user, Session session, ChatSocketConnection chatSocketConnection) {
        ChatRoom newRoom = new ChatRoomGrouped();
        newRoom.inviteToThisRoom(user, session, chatSocketConnection);
        return newRoom;
    }

}
