package ru.alcereo.pairlearning.Service.exeptions;

/**
 * Created by alcereo on 28.12.16.
 */
public class TopicServiceException extends Exception {
    public TopicServiceException() {
    }

    public TopicServiceException(String message) {
        super(message);
    }

    public TopicServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TopicServiceException(Throwable cause) {
        super(cause);
    }

    public TopicServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
