package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatRoomGrouped implements ChatRoom {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomGrouped.class);

    private final Map<Session, UserFront> sessionMap = new HashMap<>();

    private volatile boolean roomIsEmpty = true;

    private final Lock lock = new ReentrantLock();


    public boolean canInvite(UserFront user){
        //Тут боллее серьезная обработка
        return roomIsEmpty;
    }

    @Override
    public Lock getLock() {
        return lock;
    }

    @Override
    public void onMessage(String message, Session session) throws IOException {

        UserFront user = sessionMap.get(session);

        lock.lock();
        try{
            for (Session innerSession:sessionMap.keySet())
                innerSession.getBasicRemote().sendText(
                        user.getName()+": "+message);
        }finally {
            lock.unlock();
        }


    }

    @Override
    public void onClose(Session session) throws IOException {

        UserFront user = sessionMap.get(session);

        lock.lock();
        //Закрытие сокетов выполняется в том же потоке
        //Reentrant lock не сработает, придется еще синхронизировать...(
        synchronized (this) {
            try {
                for (Session innerSession : sessionMap.keySet())
                    if (innerSession != session) {
                        innerSession.getBasicRemote().sendText(String.format("%s закрыл сессию.", user.getName()));
                        sessionMap.remove(innerSession);
                        innerSession.close();
                    }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void inviteToThisRoom(UserFront user, Session session, Roomable chatSocketConnection) {

        lock.lock();
        try{
            if (sessionMap.size()>0)
                roomIsEmpty = false;

            sessionMap.put(session, user);
            chatSocketConnection.setChatRoom(this);
        }finally {
            lock.unlock();
        }

        String inviteText = String.format("%s, подключился к чату.", user.getName());

        try {
            onMessage(inviteText, session);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
