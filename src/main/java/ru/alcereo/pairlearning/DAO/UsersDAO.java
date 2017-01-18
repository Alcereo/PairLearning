package ru.alcereo.pairlearning.DAO;

import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.Entities.UserEntity;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;

import java.util.UUID;


/**
 * Класс доступа к данным о пользователях
 */
public interface UsersDAO {


    /**
     * Получение пользователя по уникальному идетификатору
     * @param uuid
     *  Уникальный иеднтификатор пользователя
     * @return
     *  Пользователь с указанным уникальным идетификатором, либо null, если не наиден
     */
    Option<UserEntity,UserDataError> findByUidOpt(UUID uuid);

    Option<UserEntity, UserDataError> findByLoginOpt(String login);

    Option<Boolean, UserDataError> loginInUse(String login);

    /**
     * Добавление в список пользователя
     * @param user
     *  Объект пользователя
     * @return
     *  Признак успешного добавления
     * @throws UserDataError
     *  Ошибка обращения к данным
     */
    Option<Boolean, UserDataError> addUserOpt(UserEntity user);

    /**
     * Устанавливает у пользователя признак active
     * @param user
     *  Ссылка на пользователя
     * @return
     *  Ссылку на новго пользователя с установленным признаком active
     * @throws UserDataError
     *  Ошибка обращения к данным
     */
    Option<UserEntity,UserDataError> makeActive(UserEntity user);

}
