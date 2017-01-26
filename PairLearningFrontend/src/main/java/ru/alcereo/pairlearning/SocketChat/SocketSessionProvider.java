package ru.alcereo.pairlearning.SocketChat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.Chat.ChatRoom;
import ru.alcereo.pairlearning.Service.Chat.RoomFabric;
import ru.alcereo.pairlearning.Service.Chat.RoomGroupedFabric;
import ru.alcereo.pairlearning.Service.Chat.exceptions.ChatInviteException;
import ru.alcereo.pairlearning.Service.SessionService;
import ru.alcereo.pairlearning.Service.models.UserFront;
import ru.alcereo.pairlearning.SocketChat.exceptions.SocketConnectionConstructionException;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

public class SocketSessionProvider{

    private static final Logger log = LoggerFactory.getLogger(SocketSessionProvider.class);

    private static final List<ChatRoom> rooms = new ArrayList<>();
    private static final RoomFabric roomFabric = new RoomGroupedFabric();
    private static SessionService sessionService;

    private ChatRoom chatRoom;

    public void setChatRoom(ChatRoom chatRoom) {
        log.debug("New chat room: {}",chatRoom);
        this.chatRoom = chatRoom;
    }

    public void setSessionService(SessionService sessionService) {
        SocketSessionProvider.sessionService = sessionService;
    }

    void addSocketSession(UserFront userFront, Session session, ChatSocketConnection chatSocketConnection)
            throws SocketConnectionConstructionException {

        for (ChatRoom chatRoom : rooms) {
            chatRoom.getLock().lock();
            try {
                if (chatRoom.tryToInvite(userFront, new SessionDecorator(session)))
                    chatSocketConnection.setChatRoom(chatRoom);
            } catch (ChatInviteException e) {
                log.warn(e.getLocalizedMessage());
                throw new SocketConnectionConstructionException("Ошибка при подключении к сервисам комнат", e);
            } finally {
                chatRoom.getLock().unlock();
            }
        }

        if (chatSocketConnection.notConnectedToRoom()) {
//                                ChatRoom chatRoom = null;
            try {
                chatRoom.tryToInvite(userFront,
                        new SessionDecorator(session));
//                                    chatRoom = roomFabric.newRoom(
//                                            user,
//                                            new SessionDecorator(session));
            } catch (ChatInviteException e) {
                log.warn(e.getLocalizedMessage());
                throw new SocketConnectionConstructionException("Ошибка подключении к сервисам комнат", e);
            }

            chatSocketConnection.setChatRoom(chatRoom);
            rooms.add(chatRoom);
        }

    }

}
