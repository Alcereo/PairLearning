package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.pairlearning.Service.Chat.exceptions.ChatInviteException;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.io.IOException;
import java.util.concurrent.locks.Lock;


/**
 * Класс чат комнаты
 */
public interface ChatRoom {


    /**
     * Получение объекта блокировки связанно с текущей комнатой
     * @return
     *  объект - блокировка связанный с текущей комнатой
     */
    Lock getLock();


    /**
     * Функция обработки события отправки сообщения пользователем в комнате
     * @param message
     *  Сообщение
     * @param handler
     *  Объект handelr, который производил отправку
     */
    void onMessage(String message, MessageHandler handler) throws IOException;


    /**
     * Функция обработки события закрытия соединения пользователем в комнате
     * @param handler
     *  Объект handelr, который производил отправку
     */
    void onClose(MessageHandler handler) throws IOException;


    /**
     * Попытка подключиться к комнате выбранным пользователем с выбранным handelr-ом.
     * Перед подключением выполняет метод {@link #canInvite(UserFront)}
     * @param user
     *  Пользователь
     * @param handler
     *  Объект обработчик отправки сообщений
     * @return
     *  Признак успешного добавления в комнату
     */
    boolean tryToInvite(UserFront user, MessageHandler handler) throws ChatInviteException;


    /**
     * Проверка возможности подключения к комнате выбранного пользователя
     * @param user
     *  Пользователь
     * @return
     *  true - если пользователь может подключиться к комнате, false - если обратное
     */
    boolean canInvite(UserFront user) throws ChatInviteException;

}
