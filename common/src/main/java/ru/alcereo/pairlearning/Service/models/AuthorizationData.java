package ru.alcereo.pairlearning.Service.models;


import java.io.*;
import java.util.Objects;

public class AuthorizationData implements Serializable{

    private static final long serialVersionUID = 1L;

    private final String login;
    private final String passHash;
    private final String sessionId;
//    private String login;
//    private String passHash;
//    private String sessionId;

    public AuthorizationData(String login, String passHash, String sessionId) {
        this.login = Objects.requireNonNull(login, "login == null!");
        this.passHash = Objects.requireNonNull(passHash, "passHash == null!");
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId == null!");
    }

    public AuthorizationData() {
        this("","","");
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

    @Override
    public String toString() {
        return "AuthorizationData{" +
                "login='" + login + '\'' +
                ", passHash='" + passHash + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
