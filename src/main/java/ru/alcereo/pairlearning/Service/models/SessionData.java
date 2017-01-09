package ru.alcereo.pairlearning.Service.models;

import java.util.Objects;

/**
 * Created by alcereo on 09.01.17.
 */
public class SessionData {

    private final String SessionId;

    public SessionData(String sessionId) {
        SessionId = Objects.requireNonNull(sessionId);
    }

    public String getSessionId() {
        return SessionId;
    }
}
