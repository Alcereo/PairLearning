package ru.alcereo.pairlearning.Service.models;

import java.io.Serializable;
import java.util.Objects;


public class RegistrationData implements Serializable{

    private static final long serialVersionUID = 1L;

    private final String sessionId;
    private final String login;
    private final String name;
    private final String password;
    private final String email;

    public RegistrationData(String sessionId, String login, String name, String password, String email) {
        this.sessionId  = Objects.requireNonNull(sessionId);
        this.login      = Objects.requireNonNull(login);
        this.name       = Objects.requireNonNull(name);
        this.password   = Objects.requireNonNull(password);
        this.email      = Objects.requireNonNull(email);
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

}
