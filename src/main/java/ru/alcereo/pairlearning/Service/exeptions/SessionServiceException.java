package ru.alcereo.pairlearning.Service.exeptions;

/**
 * Created by alcereo on 28.12.16.
 */
public class SessionServiceException extends Exception {
    public SessionServiceException() {
    }

    public SessionServiceException(String message) {
        super(message);
    }

    public SessionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionServiceException(Throwable cause) {
        super(cause);
    }

    public SessionServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
