package ru.alcereo.pairlearning.DAO.exceptions;

import java.sql.SQLException;


public class TopicRowDataError extends Exception {
    public TopicRowDataError() {
    }

    public TopicRowDataError(String message) {
        super(message);
    }

    public TopicRowDataError(String message, Throwable cause) {
        super(message, cause);
    }

    public TopicRowDataError(Throwable cause) {
        super(cause);
    }

    public TopicRowDataError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
