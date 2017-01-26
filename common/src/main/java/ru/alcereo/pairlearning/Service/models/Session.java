package ru.alcereo.pairlearning.Service.models;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        return sessionId != null ? sessionId.equals(session.sessionId) : session.sessionId == null;
    }

    @Override
    public int hashCode() {
        return sessionId != null ? sessionId.hashCode() : 0;
    }

}
