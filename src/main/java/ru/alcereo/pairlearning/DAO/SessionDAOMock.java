package ru.alcereo.pairlearning.DAO;

import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
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
    public Option<Session> getSessionOptById(String SessionId) throws SessionDataError {
        throw new NotImplementedException();
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
    public boolean insertOrUpdateSession(Session session) {
        if (sessionList.contains(session))
            sessionList.remove(session);

        sessionList.add(session);
        return true;
    }

    @Override
    public boolean deleteSessionById(String sessionId) {
        for (Session session:sessionList)
            if (session.getSessionId().equals(sessionId))
                sessionList.remove(session);
        return true;
    }

}
