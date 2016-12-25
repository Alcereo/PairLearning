package ru.alcereo.pairlearning.SocketChat;

import ru.alcereo.pairlearning.Service.Chat.MessageHandler;

import javax.websocket.Session;
import java.io.IOException;

public class SessionDecorator implements MessageHandler{

    private final Session session;

    public SessionDecorator(Session session) {
        this.session = session;
    }

    @Override
    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    @Override
    public void close() throws IOException {
        session.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionDecorator that = (SessionDecorator) o;

        return session != null ? session.equals(that.session) : that.session == null;
    }

    @Override
    public int hashCode() {
        return session != null ? session.hashCode() : 0;
    }
}
