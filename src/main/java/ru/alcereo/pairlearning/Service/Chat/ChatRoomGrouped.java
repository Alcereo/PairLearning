package ru.alcereo.pairlearning.Service.Chat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.UserFront;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatRoomGrouped implements ChatRoom {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomGrouped.class);

    private final Map<MessageHandler, UserFront> sessionMap = new HashMap<>();

    private final InviteChecker inviteChecker = new TopicInviteChecker();

    private volatile boolean roomIsEmpty = true;

    private final Lock lock = new ReentrantLock();


    public boolean canInvite(UserFront user){
        //Тут боллее серьезная обработка
        // Пока проверка, что комната пустая
        // и пользователь не тот же самый

        List<UserFront> users = new ArrayList<>(sessionMap.values());
        users.add(user);

        return (!sessionMap.values().contains(user)
                && inviteChecker.usersInvitable(users));

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
    public boolean tryToInvite(UserFront user, MessageHandler handler) {

        boolean result = false;

        lock.lock();
        try{
            if (canInvite(user)){
                sessionMap.put(handler, user);

                log.debug("User: {} invite room: {}",user.getName(),this);

                try {
                    onMessage("подключился к чату...", handler);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                result = true;
            }else {
                log.debug("User: {} NOT! invite room (by canInvite): {}",user.getName(),this);
            }
        }finally {
            lock.unlock();
        }

        return result;

    }

}
