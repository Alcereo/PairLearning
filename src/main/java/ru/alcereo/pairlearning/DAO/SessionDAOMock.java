package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.Session;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SessionDAOMock implements SessionDAO {

    private static final Set<Session> sessionList = new HashSet<>();

    @Override
    public Session getSessionById(String id) {
        Session resultS = null;

        for (Session session:sessionList)
            if (session.getSessionId().equals(id))
                resultS = session;

        return resultS;
    }

    @Override
    public Session getSessionByUser(User user) {
        Session resultS = null;

        for (Session session:sessionList)
            if (session.getUser().equals(user))
                resultS = session;

        return resultS;
    }

    @Override
    public void insertOrUpdateSession(Session session) {
        if (sessionList.contains(session))
            sessionList.remove(session);

        sessionList.add(session);
    }

    @Override
    public void deleteSession(Session session) {
        sessionList.remove(session);
    }

}
