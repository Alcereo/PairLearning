package ru.alcereo.pairlearning.DAO;


import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.Topic;
import ru.alcereo.pairlearning.Service.models.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Класс доступа к пользовательским данным связанных с изучением тем
 */
public interface TopicRowsDAO {


    /**
     * Установка признака `LEARN` для пользователя
     * @param id
     *  Идентификатор темы
     * @param user
     *  Объект пользователя
     * @param predicate
     *  Признак для установки
     * @throws TopicRowDataError
     *  Ошибка доступа к данным
     */
    void setLearnPredicate(Long id, User user, boolean predicate) throws TopicRowDataError;


    /**
     * Установка признака `TEACH` для пользователя
     * @param id
     *  Идентификатор темы
     * @param user
     *  Объект пользователя
     * @param predicate
     *  Признак для установки
     * @throws TopicRowDataError
     *  Ошибка доступа к данным
     */
    void setTeachPredicate(Long id, User user, boolean predicate) throws TopicRowDataError;


    /**
     * Получение данных связанных с пользователем о всех темах для изучения
     * @param user
     *  Объект пользователя
     * @return
     *  Список тем с признаками learn и teach
     * @throws TopicRowDataError
     *  Ошибка доступа к данным
     */
    List<TopicRow> getAllByUser(User user) throws TopicRowDataError;


    /**
     * Получение списока тем, которые пользователь выделил признаком Learn
     * @param user
     *  Объект пользователя
     * @return
     *  Список тем
     * @throws TopicRowDataError
     *  Ошибка доступа к данным
     */
    Set<Topic> getLearnTopicsByUser(User user) throws TopicRowDataError;

    /**
     * Получение списока тем, которые пользователь выделил признаком Learn
     * @param uuid
     *  uid пользователя
     * @return
     *  Список тем
     * @throws TopicRowDataError
     *  Ошибка доступа к данным
     */
    Set<Topic> getLearnTopicsByUserUID(UUID uuid) throws TopicRowDataError;

    /**
     * Получение списока тем, которые пользователь выделил признаком Teach
     * @param user
     *  Объект пользователя
     * @return
     *  Список тем
     * @throws TopicRowDataError
     *  Ошибка доступа к данным
     */
    Set<Topic> getTeachTopicsByUser(User user) throws TopicRowDataError;

    /**
     * Получение списока тем, которые пользователь выделил признаком Teach
     * @param uid
     *  uid пользователя
     * @return
     *  Список тем
     * @throws TopicRowDataError
     *  Ошибка доступа к данным
     */
    Set<Topic> getTeachTopicsByUserUID(UUID uid) throws TopicRowDataError;

}
