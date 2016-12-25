package ru.alcereo.pairlearning.Service;


import ru.alcereo.pairlearning.DAO.*;

import java.util.Objects;

public class SessionService {

    private static final UsersDAO users = new UsersDAOMock();
    private static final SessionDAO sessions = new SessionDAOMock();

    public static boolean userAuthorization(String login, String password, String sessionId) {

        boolean result = false;

        if (login != null & password != null & sessionId != null) {

            User user = users.findByLogin(login);

            if (user != null && Objects.equals(user.getPasswordHash(), password)) {
                sessions.insertOrUpdateSession(new Session(sessionId, user));
                result = true;
            }

        }

        return result;
    }

    public static boolean validateSession(String SessionId) {

        boolean result = false;

        if (SessionId != null) {
            Session session = sessions.getSessionById(SessionId);
            result = (session != null);
        }

        return result;
    }

    public static UserFront getCurrentUser(String SessionId){
        UserFront result = null;

        if (SessionId != null) {
            Session session = sessions.getSessionById(SessionId);
            if (session != null)
                result = session.getUser();
        }

        return result;
    }

}
