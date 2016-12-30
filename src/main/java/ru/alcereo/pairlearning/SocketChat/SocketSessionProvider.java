package ru.alcereo.pairlearning.SocketChat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.Chat.ChatRoom;
import ru.alcereo.pairlearning.Service.Chat.RoomFabric;
import ru.alcereo.pairlearning.Service.Chat.RoomGroupedFabric;
import ru.alcereo.pairlearning.Service.Chat.exceptions.ChatInviteException;
import ru.alcereo.pairlearning.Service.SessionService;
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;
import ru.alcereo.pairlearning.Service.exeptions.ValidateException;
import ru.alcereo.pairlearning.SocketChat.exceptions.SocketConnectionConstructionException;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SocketSessionProvider{

    private static final Logger log = LoggerFactory.getLogger(SocketSessionProvider.class);

    private static final List<ChatRoom> rooms = new ArrayList<>();
    private static final RoomFabric roomFabric = new RoomGroupedFabric();

//    private static final SessionService sessionService = new SessionService();

    static void addSocketSession(String SessionId, Session session, ChatSocketConnection chatSocketConnection)
            throws SocketConnectionConstructionException {

        if (SessionId != null) {

            try {
                if (SessionService.validateSession(SessionId)) {

                    if (!SessionService
                            .getCurrentUserOpt(SessionId)
                            .map(
                                    user ->
                        {
                            for (ChatRoom chatRoom : rooms) {
                                chatRoom.getLock().lock();
                                try {
                                    if (chatRoom.tryToInvite(user, new SessionDecorator(session)))
                                        chatSocketConnection.setChatRoom(chatRoom);
                                } catch (ChatInviteException e) {
                                    log.warn(e.getLocalizedMessage());
                                    throw new SocketConnectionConstructionException("Ошибка при подключении к сервисам комнат", e);
                                } finally {
                                    chatRoom.getLock().unlock();
                                }
                            }

                            if (chatSocketConnection.notConnectedToRoom()) {
                                ChatRoom chatRoom = null;
                                try {
                                    chatRoom = roomFabric.newRoom(
                                            user,
                                            new SessionDecorator(session));
                                } catch (ChatInviteException e) {
                                    log.warn(e.getLocalizedMessage());
                                    throw new SocketConnectionConstructionException("Ошибка подключении к сервисам комнат", e);
                                }

                                chatSocketConnection.setChatRoom(chatRoom);
                                rooms.add(chatRoom);
                            }

                            return true;

                        }).getOrElse(false)
                    )
                    throw new SocketConnectionConstructionException("Сессия валидирована, но пользователь не наиден");

                }else{

                    log.debug("Не прошел авторизацию: {}", SessionId);
                    try {
                        session.getBasicRemote().sendText("Требуется авторизация!");
                        session.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ValidateException | SessionServiceException e) {
                log.warn(e.getLocalizedMessage());
                throw new SocketConnectionConstructionException("Не удалось установить соединение",e);
            }
        }
    }

}
