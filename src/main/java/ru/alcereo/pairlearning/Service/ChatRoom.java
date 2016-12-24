package ru.alcereo.pairlearning.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.locks.Lock;

/**
 * Created by alcereo on 24.12.16.
 */
public interface ChatRoom {

    Lock getLock();

    void onMessage(String message, MessageHandler handler) throws IOException;

    void onClose(MessageHandler handler) throws IOException;

    void inviteToThisRoom(UserFront user, MessageHandler handler, Roomable roomable);

    boolean canInvite(UserFront user);

}
