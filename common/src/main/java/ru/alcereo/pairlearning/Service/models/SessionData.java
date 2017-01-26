package ru.alcereo.pairlearning.Service.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by alcereo on 09.01.17.
 */
public class SessionData implements Serializable{

    private static final long serialVersionUID = 1L;

    private final String SessionId;

    public SessionData(String sessionId) {
        SessionId = Objects.requireNonNull(sessionId);
    }

    public String getSessionId() {
        return SessionId;
    }
}
