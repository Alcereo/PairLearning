package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.*;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ru.alcereo.pairlearning.Service.RegistrationService.RegResult.*;

public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);


    private static UsersDAO users = new UsersDAOPG();
    private static SessionDAO sessions = new SessionDAOPG();


    private static final Map<Integer, User> confirmCodes = new HashMap<>();

    private SendingService sendingService;

    public RegistrationService(SendingService sendingService) {
        this.sendingService = sendingService;
    }

    public RegistrationService() {
        this(new SendingServiceMock());
    }


    public static void setUsers(UsersDAO users) {
        RegistrationService.users = users;
    }

    public static void setSessions(SessionDAO sessions) {
        RegistrationService.sessions = sessions;
    }


    public RegResult registration(
            String sessionId,
            String login,
            String name,
            String passwordHash,
            String email
    ) {

        RegResult result = ERROR;

        if (
                sessionId       != null &
                login           != null &
                name            != null &
                passwordHash    != null &
                email           != null
                )
        {

            User user = new User(UUID.randomUUID(), login, passwordHash, name, email, false);

            if (users.findByLogin(login) == null) {
                users.addUser(user);

                sessions.insertOrUpdateSession(new Session(sessionId, user));

                // Код подтверждения - 4 цифры - int от 1000 о 9999
                int confirmCode = (int) (1000 + Math.random() * (9999 - 1000));

                //отправляем код на мыло!!!!
                sendingService.send("Код подтверждения: "+confirmCode, user.getEmail());

                confirmCodes.put(confirmCode, user);

                log.debug("Зарегистрирвоали пользователя: {}, {}", user, confirmCode);

                result = RegResult.SUCCESS;

            } else
                result = RegResult.LOGIN_IN_USE;

        }

        return result;
    }

    public boolean confirmRegistration(String sessionId, Integer code) {

        boolean result = false;

        if (sessionId != null) {

            Session session = sessions.getSessionById(sessionId);
            User user = confirmCodes.get(code);

            if (session != null)
                if (user != null)
                    if (user.equals(session.getUser())) {

                        user = users.makeActive(user);
                        if (user != null) {

                            sessions.insertOrUpdateSession(new Session(sessionId, user));
                            result = true;

                            log.debug("Подтвердили регистрацию пользователя: {}", user);
                        }
                    } else {
                        log.warn("С этой сессией уже зарегистрирован пользователь, и отправлен запрос" +
                                "регистрации: текущий: {} из сессии: {}", user, session.getUser());
                    }
        }

        return result;
    }

    public enum RegResult {
        SUCCESS,
        LOGIN_IN_USE,
        EMAIL_INCORRECT,
        ERROR
    }

}
