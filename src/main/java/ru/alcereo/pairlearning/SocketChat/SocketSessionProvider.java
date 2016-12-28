package ru.alcereo.pairlearning.SocketChat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.*;
import ru.alcereo.pairlearning.Service.Chat.ChatRoom;
import ru.alcereo.pairlearning.Service.Chat.RoomFabric;
import ru.alcereo.pairlearning.Service.Chat.RoomGroupedFabric;
import ru.alcereo.pairlearning.Service.exeptions.ValidateException;
import ru.alcereo.pairlearning.Service.models.UserFront;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SocketSessionProvider{

    private static final Logger log = LoggerFactory.getLogger(SocketSessionProvider.class);

    private static final List<ChatRoom> rooms = new ArrayList<>();
    private static final RoomFabric roomFabric = new RoomGroupedFabric();

//    private static final SessionService sessionService = new SessionService();

    static void addSocketSession(String SessionId, Session session, ChatSocketConnection chatSocketConnection) throws SocketConnectionConstructionException {

        if (SessionId != null) {

            try {
                if (SessionService.validateSession(SessionId)) {

                    UserFront user = SessionService.getCurrentUser(SessionId);

                    for(ChatRoom chatRoom: rooms){
                        chatRoom.getLock().lock();
                        try{
                            if (chatRoom.tryToInvite(user, new SessionDecorator(session)))
                                chatSocketConnection.setChatRoom(chatRoom);
                        }finally {
                            chatRoom.getLock().unlock();
                        }
                    }

                    if (chatSocketConnection.notConnectedToRoom()){
                        ChatRoom chatRoom = roomFabric.newRoom(
                                user,
                                new SessionDecorator(session));

                        chatSocketConnection.setChatRoom(chatRoom);
                        rooms.add(chatRoom);
                    }

                }else{

                    log.debug("Не прошел авторизацию: {}", SessionId);
                    try {
                        session.getBasicRemote().sendText("Требуется авторизация!");
                        session.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ValidateException e) {
                log.warn(e.getLocalizedMessage());
                throw new SocketConnectionConstructionException("Не удалось установить соединение",e);
            }
        }
    }

}
