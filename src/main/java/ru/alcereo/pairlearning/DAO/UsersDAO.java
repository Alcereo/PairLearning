package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.DAO.models.User;

import java.util.List;
import java.util.UUID;


/**
 * Класс доступа к данным о пользователях
 */
public interface UsersDAO {


    /**
     * Получение списка всех пользователей
     * @return
     *  Список пользователей
     * @throws UserDataError
     *  Ошибка доступа к данным
     */
    List<User> getAll() throws UserDataError;


    /**
     * Получение пользователя по уникальному идетификатору
     * @param uuid
     *  Уникальный иеднтификатор пользователя
     * @return
     *  Пользователь с указанным уникальным идетификатором, либо null, если не наиден
     */
    User findByUid(UUID uuid) throws UserDataError;


    /**
     * Получение пользователя по логину
     * @param login
     *  Логин пользователя
     * @return
     *  Пользователь с указанным логином, либо null, если не наиден
     */
    User findByLogin(String login) throws UserDataError;


    /**
     * Добавление в список пользователя
     * @param user
     *  Объект пользователя
     * @return
     *  Признак успешного добавления
     * @throws UserDataError
     *  Ошибка обращения к данным
     */
    boolean addUser(User user) throws UserDataError;


    /**
     * Удаление пользователя
     * @param user
     *  Удаляемый пользователь
     * @return
     *  Признак успешного изменения данных
     * @throws UserDataError
     *  Ошибка обращения к данным
     */
    boolean deleteUser(User user) throws UserDataError;


    /**
     * Устанавливает у пользователя признак active
     * @param user
     *  Ссылка на пользователя
     * @return
     *  Ссылку на новго пользователя с установленным признаком active
     * @throws UserDataError
     *  Ошибка обращения к данным
     */
    User makeActive(User user) throws UserDataError;

}
