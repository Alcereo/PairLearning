package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.SessionDAOPG;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.UsersDAOPG;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.Service.exeptions.AuthorizationException;
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;
import ru.alcereo.pairlearning.Service.exeptions.ValidateException;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.security.NoSuchAlgorithmException;
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



    public static boolean userAuthorization(String login, String password, String sessionId) throws AuthorizationException {

        boolean result = false;

        if (login == null) throw new AuthorizationException(
                "Ошибка авторизации, некорректные данные. Пустой логин.",
                new NullPointerException("login == null"));

        if (password == null) throw new AuthorizationException(
                "Ошибка авторизации, некорректные данные. Пустой пароль.",
                new NullPointerException("password != null"));

        if (sessionId == null) throw new AuthorizationException(
                "Ошибка авторизации, некорректные данные. Пустой номер сесии.",
                new NullPointerException("sessionId != null"));

        try {

            User user = users.findByLogin(login);


            if (user != null){

                String passwordHash;

                try {
                    passwordHash = CryptoService.cryptPass(password, user.getUid().toString());
                } catch (NoSuchAlgorithmException e) {
                    log.warn(e.getLocalizedMessage());
                    throw new AuthorizationException(
                            "Ошибка авторизации. Ошибка при обращении к сервису хеширования",
                            e);
                }

                if (Objects.equals(user.getPasswordHash(), passwordHash)
                        && user.isActive()) {

                    sessions.insertOrUpdateSession(new Session(sessionId, user));

                    log.debug("User authorizate: {} session: {}", user, sessionId);
                    result = true;
                }
            }

        } catch (SessionDataError | UserDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new AuthorizationException(
                    "Ошибка авторизации. Ошибка при обращении к данным",
                    e);
        }

        return result;
    }

    public static boolean validateSession(String SessionId) throws ValidateException {

        boolean result = false;

        if (SessionId == null) throw new ValidateException(
                "Ошибка валидации, переданы некорректные данные.",
                new NullPointerException("SessionId == null"));

        try {
            result = (sessions.getSessionById(SessionId) != null);
        } catch (SessionDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new ValidateException("Ошибка базы данных при валидации.", e);
        }

        return result;
    }

    public static UserFront getCurrentUser(String SessionId) throws SessionServiceException {
        UserFront result = null;

        if (SessionId == null) throw new SessionServiceException(
                "Ошибка обработки, переданы некорректные данные.",
                new NullPointerException("SessionId == null"));
        try {

            Session session = sessions.getSessionById(SessionId);

            if (session != null)
                result = session.getUser();

        } catch (SessionDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new SessionServiceException(
                    "Не наидена сессия пользователя", e);
        }

        return result;
    }

    public static void deleteSession(String SessionId) throws SessionServiceException {

        try {
            sessions.deleteSessionById(SessionId);
        } catch (SessionDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new SessionServiceException(
                    "Ошибка удаления сессии", e);
        }

    }
}
