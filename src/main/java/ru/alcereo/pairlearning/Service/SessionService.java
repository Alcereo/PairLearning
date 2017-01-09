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
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;
import ru.alcereo.pairlearning.Service.models.AuthorizationData;
import ru.alcereo.pairlearning.Service.models.SessionData;

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
     * @param authData_n
     *  Данные авторизации
     * @return
     *  true - если авторизация прошла успешно, false - иначе
     */
    public Option<Boolean, SessionServiceException> userAuthorization(AuthorizationData authData_n){

        return Option.asNotNullWithExcetionOption(authData_n)
                .flatMap( authData ->
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
                    ._wrapException(SessionService::sessionServiceExceptionWrapper));
    }


    /**
     * Получение признака того, что сессия зарегистрирована в системе
     * @param sessionData_n
     *  Данные сессии
     * @return
     *  true - если текущая сессия присутствует в программе, false - иначе
     */
    public Option<Boolean, SessionServiceException> validateSession(SessionData sessionData_n){

        return Option.asNotNullWithExcetionOption(sessionData_n)
                .flatMap( sessionData ->
                    sessions
                    .getSessionOptById(sessionData.getSessionId())
                    .map(session -> true)
                    ._wrapException(SessionService::sessionServiceExceptionWrapper));
    }


    /**
     * Возвращает информаци о пользователе по идентификатору сессии
     * @param sessionData_n
     *  Данные сессии
     * @return
     *  Данные о пользователе, либо null, если отсутствует таковой
     */
    public Option<User, SessionServiceException> getCurrentUserOpt(SessionData sessionData_n) {

        return Option.asNotNullWithExcetionOption(sessionData_n)
                .flatMap( sessionData ->
                    sessions
                    .getSessionOptById(sessionData.getSessionId())
                    .map(Session::getUser)
                    ._wrapException(SessionService::sessionServiceExceptionWrapper));
    }


    /**
     * Удаление сессии
     * @param sessionData_n
     *  Данные сессии
     */
    public Option<Boolean, SessionServiceException> deleteSession(SessionData sessionData_n){

        return Option.asNotNullWithExcetionOption(sessionData_n)
                .flatMap(sessionData ->
                        Option.asOption(() -> {
                            sessions.deleteSessionById(sessionData.getSessionId());
                            return true;
                        })
                ._wrapException(SessionService::sessionServiceExceptionWrapper));
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
