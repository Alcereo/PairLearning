package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.Session;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.User;

import java.util.ArrayList;
import java.util.List;


public class SessionDAOMock implements SessionDAO {

    private static final List<Session> sessionList = new ArrayList<>();

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
    public void addSession(Session session) {
        sessionList.add(session);
    }

    @Override
    public void deleteSession(Session session) {
        sessionList.remove(session);
    }
}
