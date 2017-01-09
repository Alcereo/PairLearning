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
import ru.alcereo.pairlearning.Service.exeptions.RegistrationException;
import ru.alcereo.pairlearning.Service.models.RegistrationData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Сервис регистрации новых пользователей
 */
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private UsersDAO users;
    private SessionDAO sessions;
    private SendingService sendingService;

    private static final Map<Integer, User> confirmCodes = new HashMap<>();


    public void setSendingService(SendingService sendingService) {
        this.sendingService = sendingService;
    }

    public void setUsers(UsersDAO users) {
        this.users = users;
    }

    public void setSessions(SessionDAO sessions) {
        this.sessions = sessions;
    }


    /**
     * Регистрация нового пользователя
     * @param regDataNullable
     *  Данные регистрации
     * @return
     *  Результат регистрации
     */
    public Option<RegResult, RegistrationException> registration(RegistrationData regDataNullable){

        return Option.asOption(() -> Objects.requireNonNull(regDataNullable, "regData == null"))
        .flatMap(regData ->
            users
            .loginInUse(regData.getLogin())
            .flatMap( inUse -> {
                if (inUse)
                    return Option.asOption(RegResult.LOGIN_IN_USE);
                else {
                    UUID newUUID = UUID.randomUUID();
                    return CryptoService
                            .cryptPass(regData.getPassword(), newUUID.toString())
                            .map(passwordHash -> new User(
                                    newUUID,
                                    regData.getLogin(),
                                    passwordHash,
                                    regData.getName(),
                                    regData.getEmail(),
                                    false))
                            .flatMap(
                                    newUser ->
                                            users.addUser_Opt(newUser)

                                            .map(result -> sessions
                                                    .insertOrUpdateSession(new Session(regData.getSessionId(), newUser)))

                                            .map(result -> (int) (1000 + Math.random() * (9999 - 1000)))

                                            .map(confirmCode -> {
                                                sendingService.send("Код подтверждения: " + confirmCode, newUser.getEmail());
                                                return confirmCode;
                                            })

                                            .map(confirmCode -> {
                                                confirmCodes.put(confirmCode, newUser);
                                                return RegResult.SUCCESS;
                                            })
                            );
                }
            })
        )
        ._wrapException(RegistrationService::registrationExceptionWrapper);

    }


    /**
     * Подтверждение регистрации
     * @param sessionId
     *  Идентификатор сессиии
     * @param code
     *  Код подтверждения
     * @return
     *  true - если регистрация завершилась успешно, false - иначе
     */
    public boolean confirmRegistration(String sessionId, Integer code) throws RegistrationException {

        if (sessionId == null) throw new RegistrationException(
                "Ошибка регистрации, некоректные данные",
                new NullPointerException());

        if (code == null) throw new RegistrationException(
                "Ошибка регистрации, некоректные данные",
                new NullPointerException());

        return sessions
                .getSessionOptById(sessionId)
                .flatMap(
                        session -> Option
                                .asOption(confirmCodes.get(code))
                                .filter(user -> user.equals(session.getUser()))
                )
                .map(users::makeActive)
                .map(
                        user -> {
                            sessions.insertOrUpdateSession(new Session(sessionId, user));
                            log.debug("Подтвердили регистрацию пользователя: {}", user);
                            return true;
                        })
                ._wrapAndTrowException(cause ->
                        new RegistrationException(
                                "Ошибка регистрации при обращении к данным",
                                cause)
                )
                .getOrElse(false);

    }


    /**
     * Перечисление отображающее результат
     * регистрации пользователя
     */
    public enum RegResult {
        SUCCESS,
        LOGIN_IN_USE,
        EMAIL_INCORRECT,
        ERROR
    }

    private static RegistrationException registrationExceptionWrapper(Throwable cause) {
        log.warn(cause.getMessage());

        if (cause instanceof SessionDataError
                || cause instanceof UserDataError)
            return new RegistrationException(
                    "Ошибка регистрации. Ошибка при обращении к данным",
                    cause);

        return new RegistrationException(
                "Ошибка регистрации.",
                cause);
    }

}
