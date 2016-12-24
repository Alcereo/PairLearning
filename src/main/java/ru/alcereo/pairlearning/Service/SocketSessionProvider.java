package ru.alcereo.pairlearning.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.*;
import ru.alcereo.pairlearning.WebSocket;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SocketSessionProvider {

    private static final Logger log = LoggerFactory.getLogger(SocketSessionProvider.class);

    private static final SessionDAO sessions = new SessionDAOMock();
    private static final ChatRoomDAO chatRoomDAO = new ChatRoomDAOMock();

    private static final Map<User, Session> sessionMap = new HashMap<>();

    public static void addSocketSession(String SessionId, Session session, WebSocket webSocket ) {

        if (SessionId != null) {

            User user = sessions.getSessionById(SessionId).getUser();

            if (user!=null){
                sessionMap.put(user, session);

                ChatRoomProvider.addUserToChatRoom(user);
                User chatPartner = chatRoomDAO.getChatRoomPartner(user);

                if (chatPartner!=null){
                    try {
                        sessionMap.get(chatPartner).getBasicRemote().sendText("К чату подключе пользователь!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void sendMessageToPartner(String message, String SessionId) {

        if (SessionId != null) {
            User user = sessions.getSessionById(SessionId).getUser();
            if (user!=null){
                User chatPartner = chatRoomDAO.getChatRoomPartner(user);
                if (chatPartner!=null){
                    try {
                        sessionMap.get(chatPartner).getBasicRemote().sendText(user.getName()+": "+message);
                        sessionMap.get(user).getBasicRemote().sendText(user.getName()+": "+message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void closeSession(String httpSessionId) {
        if (httpSessionId != null) {
            User user = sessions.getSessionById(httpSessionId).getUser();

            sessionMap.remove(user);

            if (user != null) {
                User chatPartner = chatRoomDAO.getChatRoomPartner(user);

                if (chatPartner!=null) {

                    chatRoomDAO.removeChatRoom(chatPartner);

                    try {
                        Session partnerSession = sessionMap.get(chatPartner);
                        partnerSession.getBasicRemote().sendText(String.format("%s закрыл соединение", user.getName()));
                        sessionMap.remove(chatPartner);
                        partnerSession.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
