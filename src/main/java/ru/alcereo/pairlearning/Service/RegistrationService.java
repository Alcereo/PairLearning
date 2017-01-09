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
     * @param regData
     *  Данные регистрации
     * @return
     *  Результат регистрации
     */
    public RegResult registration(RegistrationData regData) throws RegistrationException {

        RegResult result;

        UUID newUUID = UUID.randomUUID();
        String passwordHash;

            passwordHash = CryptoService
                    .cryptPass(regData.getPassword(), newUUID.toString())
                    ._wrapAndTrowException(cause ->
                            new RegistrationException("Ошибка регистрации. Ошибка моудля хеширования.",
                                    cause))
                    .getOrElse("");

        User user = new User(newUUID, regData.getLogin(), passwordHash, regData.getName(), regData.getEmail(), false);

        try {

            if (users.findByLogin(regData.getLogin()) == null) {
                users.addUser(user);

                sessions.insertOrUpdateSession(new Session(regData.getSessionId(), user));

                // Код подтверждения - 4 цифры - int от 1000 о 9999
                int confirmCode = (int) (1000 + Math.random() * (9999 - 1000));

                //отправляем код на мыло!!!!
                sendingService.send("Код подтверждения: " + confirmCode, user.getEmail());

                confirmCodes.put(confirmCode, user);

                log.debug("Зарегистрирвоали пользователя: {}, {}", user, confirmCode);

                result = RegResult.SUCCESS;

            } else
                result = RegResult.LOGIN_IN_USE;

        } catch (UserDataError | SessionDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new RegistrationException("Ошибка регистрации при обращении к данным", e);
        }

        return result;
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

}
