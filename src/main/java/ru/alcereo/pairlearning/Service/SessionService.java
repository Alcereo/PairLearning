package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.exceptions.*;
import ru.alcereo.pairlearning.DAO.models.*;
import ru.alcereo.pairlearning.Service.exeptions.*;
import ru.alcereo.pairlearning.Service.models.*;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;


/**
 * Сервис работы с сессиями
 */
public class SessionService {
    
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private UsersDAO users;
    private SessionDAO sessions;

    public void setUsers(UsersDAO users) {
        this.users = users;
    }

    public void setSessions(SessionDAO sessions) {
        this.sessions = sessions;
    }


    /**
     * Попытка авторизации пользователя
     * @param login
     *  Логин пользователя
     * @param password
     *  Пароль пользователя
     * @param sessionId
     *  Идентификатор сессии
     * @return
     *  true - если авторизация прошла успешно, false - иначе
     */
    public boolean userAuthorization(String login, String password, String sessionId) throws AuthorizationException {

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
            result = users
                    .findByLoginOpt(login)
                    .map(
                            (User user) -> {

                                String passwordHash;
                                boolean lResult = false;

                                try {
                                    passwordHash = CryptoService.cryptPass(password, user.getUid().toString());
                                } catch (NoSuchAlgorithmException e) {
                                    log.warn(e.getLocalizedMessage());
                                    throw new AuthorizationException(
                                            "Ошибка авторизации. Ошибка при обращении к сервису хеширования",
                                            e);
                                }

                                try {
                                    if (Objects.equals(user.getPasswordHash(), passwordHash)
                                            && user.isActive()) {

                                        sessions.insertOrUpdateSession(new Session(sessionId, user));
                                        log.debug("User authorizate: {} session: {}", user, sessionId);
                                        lResult = true;
                                    }
                                } catch (SessionDataError e) {
                                    log.warn(e.getLocalizedMessage());
                                    throw new AuthorizationException(
                                            "Ошибка авторизации. Ошибка при обращении к данным",
                                            e);
                                }

                                return lResult;

                            }).getOrElse(false);

        } catch (UserDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new AuthorizationException(
                    "Ошибка авторизации. Ошибка при обращении к данным",
                    e);
        }

        return result;

    }


    /**
     * Получение признака того, что сессия зарегистрирована в системе
     * @param SessionId
     *  Идентификатор сессии
     * @return
     *  true - если текущая сессия присутствует в программе, false - иначе
     */
    public boolean validateSession(String SessionId) throws ValidateException {

        boolean result = false;

        if (SessionId == null) throw new ValidateException(
                "Ошибка валидации, переданы некорректные данные.",
                new NullPointerException("SessionId == null"));

        try {
            result = sessions
                    .getSessionOptById(SessionId)
                    .map(s -> true)
                    .getOrElse(false);

        } catch (SessionDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new ValidateException("Ошибка базы данных при валидации.", e);
        }

        return result;
    }


    /**
     * Возвращает информаци о пользователе по идентификатору сессии
     * @param SessionId
     *  Идентификатор сессии
     * @return
     *  Данные о пользователе, либо null, если отсутствует таковой
     */
    public Option<UserFront> getCurrentUserOpt(String SessionId) throws SessionServiceException {

        if (SessionId == null) throw new SessionServiceException(
                "Ошибка обработки, переданы некорректные данные.",
                new NullPointerException("SessionId == null"));
        try {

            return sessions
                    .getSessionOptById(SessionId)
                    .map(Session::getUser);

        } catch (SessionDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new SessionServiceException(
                    "Не наидена сессия пользователя", e);
        }

    }


    /**
     * Удаление сессии
     * @param SessionId
     *  Идентификатор удаляемой сессии
     */
    public void deleteSession(String SessionId) throws SessionServiceException {

        try {
            sessions.deleteSessionById(SessionId);
        } catch (SessionDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new SessionServiceException(
                    "Ошибка удаления сессии", e);
        }

    }
}
