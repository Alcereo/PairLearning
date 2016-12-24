package ru.alcereo.pairlearning.DAO;


import ru.alcereo.pairlearning.DAO.User;

public class Session {

    private final String sessionId;
    private final User user;

    public Session(String sessionId, User user) {
        this.sessionId = sessionId;
        this.user = user;
    }

    public Session() {
        this("",null);
    }

    public String getSessionId() {
        return sessionId;
    }

    public User getUser() {
        return user;
    }
}
