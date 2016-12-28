package ru.alcereo.pairlearning.DAO.exceptions;

import java.sql.SQLException;

/**
 * Created by alcereo on 28.12.16.
 */
public class UserDataError extends Exception {
    public UserDataError() {
    }

    public UserDataError(String message) {
        super(message);
    }

    public UserDataError(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDataError(Throwable cause) {
        super(cause);
    }

    public UserDataError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
