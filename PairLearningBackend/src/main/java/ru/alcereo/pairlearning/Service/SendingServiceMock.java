package ru.alcereo.pairlearning.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendingServiceMock implements SendingService {

    private static final Logger log = LoggerFactory.getLogger(SendingService.class);

    @Override
    public void send(String message, String email) {
        log.debug("Send message {} to {}", message, email);
    }
}
