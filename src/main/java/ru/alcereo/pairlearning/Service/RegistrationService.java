package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.SessionDAOPG;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.UsersDAOPG;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.Service.exeptions.RegistrationException;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Сервис регистрации новых пользователей
 */
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);


    private static UsersDAO users = new UsersDAOPG();
    private static SessionDAO sessions = new SessionDAOPG();


    private static final Map<Integer, User> confirmCodes = new HashMap<>();

    private static SendingService sendingService = new SendingServiceMock();


    public void setSendingService(SendingService sendingService) {
        this.sendingService = sendingService;
    }

    public static void setUsers(UsersDAO users) {
        RegistrationService.users = users;
    }

    public static void setSessions(SessionDAO sessions) {
        RegistrationService.sessions = sessions;
    }


    /**
     * Регистрация нового пользователя
     * @param sessionId
     *  Идентификатор сессиии
     * @param login
     *  Логин
     * @param name
     *  Имя
     * @param password
     *  Пароль
     * @param email
     *  Почтовый адрес
     * @return
     *  Результат регистрации
     */
    public static RegResult registration(
            String sessionId,
            String login,
            String name,
            String password,
            String email
    ) throws RegistrationException {

        RegResult result;

        if (sessionId == null) throw new RegistrationException(
                "Ошибка регистрации, некоректные данные. Пустой номер сесии.",
                new NullPointerException());

        if (login == null) throw new RegistrationException(
                "Ошибка регистрации, некоректные данные. Пустой логин.",
                new NullPointerException());

        if (name == null) throw new RegistrationException(
                "Ошибка регистрации, некоректные данные. Пустое имя.",
                new NullPointerException());

        if (password == null) throw new RegistrationException(
                "Ошибка регистрации, некоректные данные. Пустой пароль.",
                new NullPointerException());

        if (email == null) throw new RegistrationException(
                "Ошибка регистрации, некоректные данные. Пустой почтовый ящик.",
                new NullPointerException());

        UUID newUUID = UUID.randomUUID();
        String passwordHash;

        try {
            passwordHash = CryptoService.cryptPass(password, newUUID.toString());
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getLocalizedMessage());
            throw new RegistrationException("Ошибка регистрации. Ошибка моудля хеширования.",e);
        }

        User user = new User(newUUID, login, passwordHash, name, email, false);

        try {

            if (users.findByLogin(login) == null) {
                users.addUser(user);

                sessions.insertOrUpdateSession(new Session(sessionId, user));

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
    public static boolean confirmRegistration(String sessionId, Integer code) throws RegistrationException {

        if (sessionId == null) throw new RegistrationException(
                "Ошибка регистрации, некоректные данные",
                new NullPointerException());

        if (code == null) throw new RegistrationException(
                "Ошибка регистрации, некоректные данные",
                new NullPointerException());

        try {

            return sessions
            .getSessionOptById(sessionId)
            .flatMap(
              session ->
                      Option.asOption(confirmCodes.get(code)).map(
                         user ->
                         {
                             try {
                                 if (user.equals(session.getUser())) {

                                     user = users.makeActive(user);
                                     if (user != null) {

                                         sessions.insertOrUpdateSession(new Session(sessionId, user));

                                         log.debug("Подтвердили регистрацию пользователя: {}", user);
                                     }
                                 } else {
                                     log.warn("С этой сессией уже зарегистрирован пользователь, и отправлен запрос" +
                                             "регистрации: текущий: {} из сессии: {}", user, session.getUser());
                                 }
                             } catch (UserDataError | SessionDataError e) {
                                 log.warn(e.getLocalizedMessage());
                                 throw new RegistrationException("Ошибка регистрации при обращении к данным", e);
                             }

                             return true;
                         }
                      )
            ).getOrElse(false);

        }catch (SessionDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new RegistrationException("Ошибка регистрации при обращении к данным", e);
        }

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
