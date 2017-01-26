package ru.alcereo.pairlearning.Service.Chat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.Chat.exceptions.ChatInviteException;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatRoomGrouped implements ChatRoom {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomGrouped.class);

    private InviteChecker inviteChecker;

    private final Map<MessageHandler, UserFront> sessionMap = new HashMap<>();
    private final Lock lock = new ReentrantLock();

    public void setInviteChecker(InviteChecker inviteChecker) {
        this.inviteChecker = inviteChecker;
    }

    public boolean canInvite(UserFront user) throws ChatInviteException {
        boolean result;

        List<UserFront> users = new ArrayList<>(sessionMap.values());
        users.add(user);


        try {
            result = (!sessionMap.values().contains(user)
                    &&
                    inviteChecker.usersInvitable(users)
                    .throwException()
                    .getOrElse(false)
            );
        } catch (ChatInviteException e) {
            log.warn(e.getLocalizedMessage());
            throw new ChatInviteException("Ошибка проверки доступа в комнату. Ошибка сервиса тем. "+e.getLocalizedMessage());
        }

        return result;

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
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean tryToInvite(UserFront user, MessageHandler handler) throws ChatInviteException {

        boolean result = false;

        lock.lock();
        try{
            if (canInvite(user)){
                sessionMap.put(handler, user);

                log.debug("User: {} invite room: {}",user.getName(),this);

                try {
                    onMessage("подключился к чату...", handler);
                } catch (IOException e) {
                    log.warn(e.getLocalizedMessage());
                    throw new ChatInviteException("Ошибка отправки сообщения",e);
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


    static class ChatRoomFactory implements RoomFabric{

        @Override
        public ChatRoom newRoom(UserFront user, MessageHandler handler) throws ChatInviteException {
            ChatRoom newRoom = new ChatRoomGrouped();
            newRoom.tryToInvite(user, handler);
            return newRoom;
        }
    }

}
