package ru.alcereo.pairlearning;

import org.apache.tomcat.websocket.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.ChatRoom;
import ru.alcereo.pairlearning.Service.SessionProvider;
import ru.alcereo.pairlearning.Service.SocketSessionProvider;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/socket")
public class WebSocket {

    private static final Logger log = LoggerFactory.getLogger(WebSocket.class);

    private ChatRoom chatRoom;

    {
        log.debug("Создан инстанс WebSocket: {}", this);
    }

    @OnOpen
    public void onOpen(Session session){

        WsSession wsSession = (WsSession) session;
        log.debug("Новое содениение: {}", wsSession.getHttpSessionId());


        if (SessionProvider.validateSession(wsSession.getHttpSessionId())) {
            SocketSessionProvider.addSocketSession(wsSession.getHttpSessionId(), session, this);

            try {
                session.getBasicRemote().sendText("Подключение установлено!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.debug("Не прошел авторизацию: {}", wsSession.getHttpSessionId());
            try {
                session.getBasicRemote().sendText("Требуется авторизация!");
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session){

        WsSession wsSession = (WsSession) session;
        log.debug("Сообщение:{} от соединения: {}", message, wsSession.getHttpSessionId());

        SocketSessionProvider.sendMessageToPartner(message, wsSession.getHttpSessionId());

    }

    @OnClose
    public void onClose(Session session){

        WsSession wsSession = (WsSession) session;
        log.debug("Соединение разорвано: {}", wsSession.getHttpSessionId());

        SocketSessionProvider.closeSession(wsSession.getHttpSessionId());
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
