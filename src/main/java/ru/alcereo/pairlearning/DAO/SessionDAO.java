package ru.alcereo.pairlearning.DAO;

import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.Service.models.Session;
import ru.alcereo.pairlearning.Service.models.User;

/**
 * Класс доступа к данным сессий
 */
public interface SessionDAO {


    /**
     * Получение объекта сессии по ее идентификатору
     * @param SessionId
     *  Идентификатор сессиии.
     * @return
     *  Объект сесси
     * @throws SessionDataError
     *  Ошибка доступа к данным о сессиях
     */
    Session getSessionById(String SessionId) throws SessionDataError;

    Option<Session, SessionDataError> getSessionOptById(String SessionId);

    /**
     * Получение объекта сессии по пользователю
     * @param user
     *  Объект пользователя
     * @return
     *  Объект сесси
     * @throws SessionDataError
     *  Ошибка доступа к данным о сессиях
     */
    Session getSessionByUser(User user) throws SessionDataError;


    /**
     * Вставка объекта сессии или обновление объекта, если такой уже присутствует
     * @param session
     *  Объект сессии
     * @return
     *  Признак успешного обновления данных
     * @throws SessionDataError
     *  Ошибка доступа к данным о сессиях
     */
    boolean insertOrUpdateSession(Session session) throws SessionDataError;


    /**
     * Удаление сессии по идентификатору
     * @param sessionId
     *  Значение идентификатора
     * @return
     *  Признак успешного обновления данных
     * @throws SessionDataError
     *  Ошибка доступа к данным о сессиях
     */
    boolean deleteSessionById(String sessionId) throws SessionDataError;

}
