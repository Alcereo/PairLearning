package ru.alcereo.pairlearning.SocketChat;

import ru.alcereo.pairlearning.Service.exeptions.ValidateException;


public class SocketConnectionConstructionException extends Exception {

    public SocketConnectionConstructionException() {
    }

    public SocketConnectionConstructionException(String message) {
        super(message);
    }

    public SocketConnectionConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketConnectionConstructionException(Throwable cause) {
        super(cause);
    }

    public SocketConnectionConstructionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
