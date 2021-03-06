package ru.alcereo.pairlearning.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.Entities.UserEntity;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.exeptions.RegistrationException;
import ru.alcereo.pairlearning.Service.models.RegistrationData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Сервис регистрации новых пользователей
 */
@Service
public class RegistrationServiceIml implements RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationServiceIml.class);

    private UsersDAO users;
    private EntityMapper entityMapper;

    private static final Map<Integer, User> confirmCodes = new HashMap<>();

    @Autowired
    public void setUsers(UsersDAO users) {
        this.users = users;
    }

    @Autowired
    public void setEntityMapper(EntityMapper entityMapper) {
        this.entityMapper = entityMapper;
    }

//     * -------------------------------------------------------- *
//     *                     ОСНОВНЫЕ ФУНКЦИИ                     *
//     * -------------------------------------------------------- *

    /**
     * Регистрация нового пользователя
     * @param regDataNullable
     *  Данные регистрации
     * @return
     *  Результат регистрации
     */
    @Override
    public Option<RegResult, RegistrationException> registration(RegistrationData regDataNullable){
        return Option.asNotNullWithExceptionOption(regDataNullable)
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
                                    true))
                            .flatMap(
                                    newUser ->
                                            users.addUserOpt(
                                                    entityMapper.map(newUser, UserEntity.class)
                                            )
                                            .map(result -> result ? RegResult.SUCCESS : RegResult.ERROR)
                            );
                }
            })
        )
        .wrapException(RegistrationServiceIml::registrationExceptionWrapper);
    }

//     * -------------------------------------------------------- *
//     *                       СЛУЖЕБНЫЕ                          *
//     * -------------------------------------------------------- *

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
