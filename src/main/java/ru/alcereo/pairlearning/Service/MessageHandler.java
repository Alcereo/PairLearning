package ru.alcereo.pairlearning.Service;


import java.io.IOException;

public interface MessageHandler {

    void sendMessage(String message) throws IOException;

    void close() throws IOException;

}
