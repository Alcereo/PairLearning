package ru.alcereo.pairlearning.SocketChat;

import org.apache.tomcat.websocket.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.server.standard.SpringConfigurator;
import ru.alcereo.pairlearning.Service.Chat.ChatRoom;
import ru.alcereo.pairlearning.Service.models.UserFront;
import ru.alcereo.pairlearning.SocketChat.exceptions.SocketConnectionConstructionException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;

@ServerEndpoint(value = "/socket", configurator = SpringConfigurator.class)
public class ChatSocketConnection {

    private static final Logger log = LoggerFactory.getLogger(ChatSocketConnection.class);

    {
        log.debug("Создан инстанс ChatSocketConnection: {}", this);
    }

    private ChatRoom chatRoom;
    private SocketSessionProvider socketSessionProvider;

    @Autowired
    public void setSocketSessionProvider(SocketSessionProvider socketSessionProvider) {
        this.socketSessionProvider = socketSessionProvider;
    }


    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public boolean notConnectedToRoom(){
        return Objects.isNull(chatRoom);
    }

    @OnOpen
    public void onOpen(Session session){

        UserFront userFront = (UserFront)((UsernamePasswordAuthenticationToken) session.getUserPrincipal()).getPrincipal();
        log.debug("Новое содениение: {}", userFront.getLogin());

        try {
            socketSessionProvider.addSocketSession(userFront, session, this);

        } catch (SocketConnectionConstructionException e) {

            log.warn(e.getLocalizedMessage());
            try {
                session.getBasicRemote().sendText(e.getLocalizedMessage());
                session.close();
            } catch (IOException e1) {
                log.error(e1.getLocalizedMessage());
            }

        }

    }

    @OnMessage
    public void onMessage(String message, Session session){

        WsSession wsSession = (WsSession) session;
        log.debug("Сообщение:{} от соединения: {}", message, wsSession.getHttpSessionId());

        try {
            chatRoom.onMessage(message, new SessionDecorator(session));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnClose
    public void onClose(Session session){

        WsSession wsSession = (WsSession) session;
        log.debug("Соединение разорвано: {}", wsSession.getHttpSessionId());

        try {
            chatRoom.onClose(new SessionDecorator(session));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
