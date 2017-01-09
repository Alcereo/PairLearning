package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.Service.exeptions.AuthorizationException;
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;
import ru.alcereo.pairlearning.Service.models.AuthorizationData;
import ru.alcereo.pairlearning.Service.models.SessionData;
import sun.security.validator.ValidatorException;

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
     * @param authData
     *  Данные авторизации
     * @return
     *  true - если авторизация прошла успешно, false - иначе
     */
    public Option<Boolean, AuthorizationException> userAuthorization(AuthorizationData authData){

        return Option.asOption(() -> Objects.requireNonNull(authData, "authData == null"))
                .flatMap( value ->
                    users
                    .findByLoginOpt(authData.getLogin())
                    .flatMap(
                            user -> CryptoService.cryptPass(authData.getPassHash(), user.getUid().toString())
                                    .filter(passwordHash ->
                                            Objects.equals(user.getPasswordHash(), passwordHash)
                                                    && user.isActive())
                                    .map(passwordHash -> {
                                        sessions.insertOrUpdateSession(new Session(authData.getSessionId(), user));
                                        log.debug("User authorizate: {} session: {}", user, authData.getSessionId());
                                        return true;
                                    }))
                    ._wrapException(SessionService::authorizationExceptionWrapper));
    }


    /**
     * Получение признака того, что сессия зарегистрирована в системе
     * @param sessionData
     *  Данные сессии
     * @return
     *  true - если текущая сессия присутствует в программе, false - иначе
     */
    public Option<Boolean, ValidatorException> validateSession(SessionData sessionData){

        return Option.asOption(() -> Objects.requireNonNull(sessionData, "sessionData == null"))
                .flatMap( value ->
                    sessions
                    .getSessionOptById(sessionData.getSessionId())
                    .map(s -> true)
                    ._wrapException(SessionService::validationExceptionWrapper));
    }


    /**
     * Возвращает информаци о пользователе по идентификатору сессии
     * @param sessionData
     *  Данные сессии
     * @return
     *  Данные о пользователе, либо null, если отсутствует таковой
     */
    public Option<User, SessionServiceException> getCurrentUserOpt(SessionData sessionData) {

        return Option.asOption(() -> Objects.requireNonNull(sessionData, "sessionData == null"))
                .flatMap( value ->
                    sessions
                    .getSessionOptById(sessionData.getSessionId())
                    .map(Session::getUser)
                    ._wrapException(SessionService::sessionServiceExceptionWrapper));
    }


    /**
     * Удаление сессии
     * @param sessionData
     *  Данные сессии
     */
    public Option<Boolean, SessionServiceException> deleteSession(SessionData sessionData){

        return Option.asOption(() -> Objects.requireNonNull(sessionData, "sessionData == null"))
                .flatMap(value ->
                        Option.asOption(() -> {
                            sessions.deleteSessionById(sessionData.getSessionId());
                            return true;
                        })
                ._wrapException(SessionService::sessionServiceExceptionWrapper));
    }

    private static AuthorizationException authorizationExceptionWrapper(Throwable cause) {
        log.warn(cause.getMessage());

        if (cause instanceof NoSuchAlgorithmException)
            return new AuthorizationException(
                    "Ошибка авторизации. Ошибка при обращении к сервису хеширования",
                    cause);

        if (cause instanceof SessionDataError
                || cause instanceof UserDataError)
            return new AuthorizationException(
                    "Ошибка авторизации. Ошибка при обращении к данным",
                    cause);

        return new AuthorizationException(
                "Ошибка авторизации.",
                cause);
    }

    private static ValidatorException validationExceptionWrapper(Throwable cause) {
        log.warn(cause.getMessage());

        if (cause instanceof NoSuchAlgorithmException)
            return new ValidatorException(
                    "Ошибка валидации. Ошибка при обращении к сервису хеширования",
                    cause);

        if (cause instanceof SessionDataError
                || cause instanceof UserDataError)
            return new ValidatorException(
                    "Ошибка валидации. Ошибка при обращении к данным",
                    cause);

        return new ValidatorException(
                "Ошибка валидации.",
                cause);
    }

    private static SessionServiceException sessionServiceExceptionWrapper(Throwable cause) {
        log.warn(cause.getMessage());

        if (cause instanceof NoSuchAlgorithmException)
            return new SessionServiceException(
                    "Ошибка сервиса сессий. Ошибка при обращении к сервису хеширования",
                    cause);

        if (cause instanceof SessionDataError
                || cause instanceof UserDataError)
            return new SessionServiceException(
                    "Ошибка сервиса сессий. Ошибка при обращении к данным",
                    cause);

        return new SessionServiceException(
                "Ошибка сервиса сессий.",
                cause);
    }

}
