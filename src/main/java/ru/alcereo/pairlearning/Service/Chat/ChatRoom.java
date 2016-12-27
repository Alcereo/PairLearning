package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.pairlearning.Service.UserFront;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

/**
 * Created by alcereo on 24.12.16.
 */
public interface ChatRoom {

    Lock getLock();

    void onMessage(String message, MessageHandler handler) throws IOException;

    void onClose(MessageHandler handler) throws IOException;

    boolean tryToInvite(UserFront user, MessageHandler handler);

    boolean canInvite(UserFront user);

}
