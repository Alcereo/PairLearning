package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.pairlearning.Service.UserFront;

public class RoomGroupedFabric implements RoomFabric {

    @Override
    public ChatRoom newRoom(UserFront user, MessageHandler handler) {
        ChatRoom newRoom = new ChatRoomGrouped();
        newRoom.tryToInvite(user, handler);
        return newRoom;
    }

}
