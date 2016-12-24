package ru.alcereo.pairlearning.SocketChat;

import org.apache.tomcat.websocket.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.ChatRoom;
import ru.alcereo.pairlearning.Service.Roomable;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;

@ServerEndpoint(value = "/socket")
public class ChatSocketConnection implements Roomable {

    private static final Logger log = LoggerFactory.getLogger(ChatSocketConnection.class);

    {
        log.debug("Создан инстанс ChatSocketConnection: {}", this);
    }

    private ChatRoom chatRoom;

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public boolean roomIsEmpty(){
        return Objects.isNull(chatRoom);
    }

    @OnOpen
    public void onOpen(Session session){

        WsSession wsSession = (WsSession) session;
        log.debug("Новое содениение: {}", wsSession.getHttpSessionId());

        SocketSessionProvider.addSocketSession(wsSession.getHttpSessionId(), session, this);

    }

    @OnMessage
    public void onMessage(String message, Session session){

        WsSession wsSession = (WsSession) session;
        log.debug("Сообщение:{} от соединения: {}", message, wsSession.getHttpSessionId());

        try {
            chatRoom.onMessage(message, session);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnClose
    public void onClose(Session session){

        WsSession wsSession = (WsSession) session;
        log.debug("Соединение разорвано: {}", wsSession.getHttpSessionId());

        try {
            chatRoom.onClose(session);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
