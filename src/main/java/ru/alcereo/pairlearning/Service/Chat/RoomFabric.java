package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.pairlearning.Service.Chat.exceptions.ChatInviteException;
import ru.alcereo.pairlearning.Service.models.UserFront;

public interface RoomFabric {

    ChatRoom newRoom(UserFront user, MessageHandler handler) throws ChatInviteException;

}
