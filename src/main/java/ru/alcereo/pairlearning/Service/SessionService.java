package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.Service.exeptions.AuthorizationException;
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;
import ru.alcereo.pairlearning.Service.exeptions.ValidateException;
import ru.alcereo.pairlearning.Service.models.UserFront;

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
    public Option<Boolean, AuthorizationException> userAuthorization(String login, String password, String sessionId){

        if (login == null)
            return Option.exceptOpt(
                new AuthorizationException(
                        "Ошибка авторизации, некорректные данные. Пустой логин.",
                        new NullPointerException("login == null")));

        if (password == null)
            return Option.exceptOpt(
                    new AuthorizationException(
                            "Ошибка авторизации, некорректные данные. Пустой пароль.",
                            new NullPointerException("password != null")));

        if (sessionId == null)
            return Option.exceptOpt(
                    new AuthorizationException(
                            "Ошибка авторизации, некорректные данные. Пустой номер сесии.",
                            new NullPointerException("sessionId != null")));

        return users
                .findByLoginOpt(login)
                .flatMap(
                        user -> Option.asOption("")
                                .map(value -> CryptoService.cryptPass(password, user.getUid().toString()))
                                .filter(passwordHash ->
                                        Objects.equals(user.getPasswordHash(), passwordHash)
                                                && user.isActive())
                                .map(passwordHash -> {
                                    sessions.insertOrUpdateSession(new Session(sessionId, user));
                                    log.debug("User authorizate: {} session: {}", user, sessionId);
                                    return true;
                                })
                )
                ._wrapException(e -> {
                    if (e instanceof NoSuchAlgorithmException)
                        return new AuthorizationException(
                                "Ошибка авторизации. Ошибка при обращении к сервису хеширования",
                                e);

                    if (e instanceof SessionDataError
                            || e instanceof UserDataError)
                        return new AuthorizationException(
                                "Ошибка авторизации. Ошибка при обращении к данным",
                                e);

                    return new AuthorizationException(
                            "Ошибка авторизации.",
                            e);
                });
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

        result = sessions
                .getSessionOptById(SessionId)
                .map(s -> true)
                ._wrapAndTrowException(e -> {
                    log.warn(e.getLocalizedMessage());
                    return new ValidateException("Ошибка базы данных при валидации.", e);
                })
                .getOrElse(false);

        return result;
    }


    /**
     * Возвращает информаци о пользователе по идентификатору сессии
     * @param SessionId
     *  Идентификатор сессии
     * @return
     *  Данные о пользователе, либо null, если отсутствует таковой
     */
    public Option<UserFront, SessionServiceException> getCurrentUserOpt(String SessionId) {

        if (SessionId == null)
            return Option.exceptOpt(
                    new SessionServiceException(
                            "Ошибка обработки, переданы некорректные данные.",
                            new NullPointerException("SessionId == null")));

        return sessions
                .getSessionOptById(SessionId)
                ._wrapException(cause -> new SessionServiceException(
                        "Не наидена сессия пользователя", cause))
                .map(Session::getUser);

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
