package ru.alcereo.pairlearning.SocketChat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.*;
import ru.alcereo.pairlearning.Service.Chat.ChatRoom;
import ru.alcereo.pairlearning.Service.Chat.RoomFabric;
import ru.alcereo.pairlearning.Service.Chat.RoomGroupedFabric;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SocketSessionProvider{

    private static final Logger log = LoggerFactory.getLogger(SocketSessionProvider.class);

    private static final List<ChatRoom> rooms = new ArrayList<>();
    private static final RoomFabric roomFabric = new RoomGroupedFabric();

    static void addSocketSession(String SessionId, Session session, ChatSocketConnection chatSocketConnection) {

        if (SessionId != null) {

            if (SessionService.validateSession(SessionId)) {

                UserFront user = SessionService.getCurrentUser(SessionId);

                for(ChatRoom chatRoom: rooms){
                    chatRoom.getLock().lock();
                    try{
                        if (chatRoom.canInvite(user)) {
                            chatRoom.inviteToThisRoom(user, new SessionDecorator(session));

                            chatSocketConnection.setChatRoom(chatRoom);
                        }

                    }finally {
                        chatRoom.getLock().unlock();
                    }
                }

                if (chatSocketConnection.roomIsEmpty()){
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
        }
    }

}
