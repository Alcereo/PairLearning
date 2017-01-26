package ru.alcereo.pairlearning.Service.Chat.exceptions;

public class ChatInviteException extends Exception {
    public ChatInviteException() {
    }

    public ChatInviteException(String message) {
        super(message);
    }

    public ChatInviteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatInviteException(Throwable cause) {
        super(cause);
    }

    public ChatInviteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
