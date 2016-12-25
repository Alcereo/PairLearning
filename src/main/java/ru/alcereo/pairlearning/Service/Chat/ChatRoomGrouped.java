package ru.alcereo.pairlearning.Service.Chat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.UserFront;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatRoomGrouped implements ChatRoom {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomGrouped.class);

    private final Map<MessageHandler, UserFront> sessionMap = new HashMap<>();

    private volatile boolean roomIsEmpty = true;

    private final Lock lock = new ReentrantLock();


    public boolean canInvite(UserFront user){
        //Тут боллее серьезная обработка
        // Пока проверка, что комната пустая
        // и пользователь не тот же самый
        return (roomIsEmpty && !sessionMap.values().contains(user));
    }

    @Override
    public Lock getLock() {
        return lock;
    }

    @Override
    public void onMessage(String message, MessageHandler handler) throws IOException {

        UserFront user = sessionMap.get(handler);

        lock.lock();
        try{
            for (MessageHandler innerSession:sessionMap.keySet())
                innerSession.sendMessage(
                        user.getName()+": "+message);
        }finally {
            lock.unlock();
        }

    }

    @Override
    public void onClose(MessageHandler handler) throws IOException {

        UserFront user = sessionMap.get(handler);

        lock.lock();
        try {
            for (MessageHandler innerSession : sessionMap.keySet())
                if (!innerSession.equals(handler)) {
                    innerSession.sendMessage(String.format("%s закрыл сессию.", user.getName()));

                    new Thread(() -> {
                        try {
                            innerSession.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }

            sessionMap.clear();
            roomIsEmpty = true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void inviteToThisRoom(UserFront user, MessageHandler handler) {

        lock.lock();
        try{
            if (sessionMap.size()>0)
                roomIsEmpty = false;

            sessionMap.put(handler, user);

            log.debug("User: {} invite room: {}",user.getName(),this);
        }finally {
            lock.unlock();
        }

        try {
            onMessage("подключился к чату...", handler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
