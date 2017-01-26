package ru.alcereo.pairlearning.Service.models;

import java.util.Objects;

public class ConfirmationData {

    private final String sessionId;
    private final int code;

    public ConfirmationData(String sessionId, int code) {
        this.sessionId  = Objects.requireNonNull(sessionId);
        this.code       = Objects.requireNonNull(code);
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getCode() {
        return code;
    }
}
