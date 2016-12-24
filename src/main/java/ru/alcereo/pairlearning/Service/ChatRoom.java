package ru.alcereo.pairlearning.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.locks.Lock;

/**
 * Created by alcereo on 24.12.16.
 */
public interface ChatRoom {

    Lock getLock();

    void onMessage(String message, Session session) throws IOException;

    void onClose(Session session) throws IOException;

    void inviteToThisRoom(UserFront user, Session session, Roomable chatSocketConnection);

    boolean canInvite(UserFront user);

}
