package ru.alcereo.pairlearning.DAO;

public interface SessionDAO {

    Session getSessionById(String id);
    Session getSessionByUser(User user);

    void insertOrUpdateSession(Session session);
    void deleteSession(Session session);

}
