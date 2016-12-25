package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.pairlearning.Service.UserFront;

public interface RoomFabric {

    ChatRoom newRoom(UserFront user, MessageHandler handler);

}
