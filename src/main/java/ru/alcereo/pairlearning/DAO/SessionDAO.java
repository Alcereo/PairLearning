package ru.alcereo.pairlearning.DAO;

public interface SessionDAO {

    Session getSessionById(String id);
    Session getSessionByUser(User user);

    boolean insertOrUpdateSession(Session session);
    boolean deleteSession(Session session);

}
