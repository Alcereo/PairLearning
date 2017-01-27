package ru.alcereo.pairlearning.Service;


import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;
import ru.alcereo.pairlearning.Service.models.AuthorizationData;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;


/**
 * Сервис работы с сессиями
 */
@Service
public class SessionServiceImpl implements SessionService {
    
    private static final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);

    private UsersDAO users;

    private MapperFacade mapperFacade;

    @Autowired
    public void setUsers(UsersDAO users) {
        this.users = users;
    }

    @Autowired
    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

//     * -------------------------------------------------------- *
//     *                     ОСНОВНЫЕ ФУНКЦИИ                     *
//     * -------------------------------------------------------- *

    /**
     * Попытка авторизации пользователя
     * @param authData_n
     *  Данные авторизации
     * @return
     *  true - если авторизация прошла успешно, false - иначе
     */
    @Override
    public Option<User, SessionServiceException> userAuthorization(AuthorizationData authData_n){

        return Option.asNotNullWithExceptionOption(authData_n)
                .flatMap( authData ->
                    users
                    .findByLoginOpt(authData.getLogin())
                    .map(userEntity -> mapperFacade.map(userEntity, User.class))
                    .flatMap(
                            user -> CryptoService.cryptPass(authData.getPassHash(), user.getUid().toString())
                                    .filter(passwordHash ->
                                            Objects.equals(user.getPasswordHash(), passwordHash)
                                                    && user.isActive())
                                    .map(passwordHash -> user)
                    )
                    .wrapException(SessionServiceImpl::sessionServiceExceptionWrapper));
    }

//     * -------------------------------------------------------- *
//     *                       СЛУЖЕБНЫЕ                          *
//     * -------------------------------------------------------- *

    private static SessionServiceException sessionServiceExceptionWrapper(Throwable cause) {
        log.warn(cause.getLocalizedMessage());

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
