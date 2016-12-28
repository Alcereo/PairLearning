package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;

import java.sql.SQLException;

public interface SessionDAO {

    Session getSessionById(String id) throws SessionDataError;
    Session getSessionByUser(User user) throws SessionDataError;

    boolean insertOrUpdateSession(Session session) throws SessionDataError;
    boolean deleteSessionById(String sessionId) throws SessionDataError;

}
