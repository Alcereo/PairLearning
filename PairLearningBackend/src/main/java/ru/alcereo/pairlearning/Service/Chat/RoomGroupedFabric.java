package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.pairlearning.Service.Chat.exceptions.ChatInviteException;
import ru.alcereo.pairlearning.Service.models.UserFront;

public class RoomGroupedFabric implements RoomFabric {

    @Override
    public ChatRoom newRoom(UserFront user, MessageHandler handler) throws ChatInviteException {
        ChatRoom newRoom = new ChatRoomGrouped();
        newRoom.tryToInvite(user, handler);
        return newRoom;
    }

}
