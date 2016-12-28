package ru.alcereo.pairlearning.Service;


/**
 * Сервис отправки сообщений пользователю
 */
public interface SendingService {

    /**
     * Отправка сообщения пользователю
     * @param message
     *  Сообщение
     * @param email
     *  Адрес отправления
     */
    void send(String message, String email);
}
