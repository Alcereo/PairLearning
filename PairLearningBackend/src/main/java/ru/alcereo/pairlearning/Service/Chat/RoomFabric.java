package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.pairlearning.Service.Chat.exceptions.ChatInviteException;
import ru.alcereo.pairlearning.Service.models.UserFront;

/**
 * Фабрика создания новых комнат
 */
public interface RoomFabric {

    /**
     * Создание новой комнаты с указанным пользователем в ней сразу
     * @param user
     *  Пользователь
     * @param handler
     *  Обработчик сообщений
     * @return
     *  Новую комнату с выбранным пользователем в ней
     */
    ChatRoom newRoom(UserFront user, MessageHandler handler) throws ChatInviteException;

}
