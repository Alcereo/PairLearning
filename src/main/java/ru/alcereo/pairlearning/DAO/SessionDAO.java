package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;

public interface SessionDAO {

    Session getSessionById(String id);
    Session getSessionByUser(User user);

    boolean insertOrUpdateSession(Session session);
    boolean deleteSessionById(String sessionId);

}
