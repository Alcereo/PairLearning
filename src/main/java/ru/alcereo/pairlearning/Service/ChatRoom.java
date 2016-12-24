package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.SessionDAOMock;
import ru.alcereo.pairlearning.DAO.User;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom {

    private static final Logger log = LoggerFactory.getLogger(ChatRoom.class);

    private static final SessionDAO sessions = new SessionDAOMock();

    private final Map<String, Session> sessionMap = new HashMap<>();

    private boolean roomIsEmpty = true;

    public void addToRoom(String sessionId, Session session){

    }

}
