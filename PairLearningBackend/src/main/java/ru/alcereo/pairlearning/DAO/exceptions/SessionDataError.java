package ru.alcereo.pairlearning.DAO.exceptions;

import java.sql.SQLException;

/**
 * Created by alcereo on 28.12.16.
 */
public class SessionDataError extends Exception {
    public SessionDataError() {
    }

    public SessionDataError(String message) {
        super(message);
    }

    public SessionDataError(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionDataError(Throwable cause) {
        super(cause);
    }

    public SessionDataError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
