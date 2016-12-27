package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.*;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;

import java.util.Objects;

public class SessionService {
    
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private static UsersDAO users = new UsersDAOPG();
    private static SessionDAO sessions = new SessionDAOPG();

    public static void setUsers(UsersDAO users) {
        SessionService.users = users;
    }

    public static void setSessions(SessionDAO sessions) {
        SessionService.sessions = sessions;
    }



    public static boolean userAuthorization(String login, String password, String sessionId) {

        boolean result = false;

        if (login != null & password != null & sessionId != null) {

            User user = users.findByLogin(login);

            if (user != null
                    && Objects.equals(user.getPasswordHash(), password)
                    && user.isActive()) {
                sessions.insertOrUpdateSession(new Session(sessionId, user));
                
                log.debug("User authorizate: {} session: {}", user, sessionId);
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

    public static void deleteSession(String SessionId) {

        sessions.deleteSessionById(SessionId);

    }
}
