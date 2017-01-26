package ru.alcereo.pairlearning.Service.Chat;


import java.io.IOException;


/**
 * Обработчик сообщений в комнате чата
 */
public interface MessageHandler {

    /**
     * Отправка сообщения
     * @param message
     *  сообщение
     */
    void sendMessage(String message) throws IOException;

    /**
     * Закрыть соединение
     */
    void close() throws IOException;

}
