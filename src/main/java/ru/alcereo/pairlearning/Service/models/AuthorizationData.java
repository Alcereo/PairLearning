package ru.alcereo.pairlearning.Service.models;


import java.util.Objects;

public class AuthorizationData {

    private final String login;
    private final String passHash;
    private final String sessionId;

    public AuthorizationData(String login, String passHash, String sessionId) {
        this.login = Objects.requireNonNull(login, "login == null!");
        this.passHash = Objects.requireNonNull(passHash, "passHash == null!");
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId == null!");
    }

    public String getLogin() {
        return login;
    }

    public String getPassHash() {
        return passHash;
    }

    public String getSessionId() {
        return sessionId;
    }
}
