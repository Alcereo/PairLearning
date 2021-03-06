package ru.alcereo.pairlearning.DAO;


import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.Entities.TopicEntity;
import ru.alcereo.pairlearning.DAO.Entities.TopicRowEntity;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.UserFront;

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
    Option<Boolean, TopicRowDataError> setLearnPredicate(Long id, UserFront user, boolean predicate);

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
    Option<Boolean, TopicRowDataError> setTeachPredicate(Long id, UserFront user, boolean predicate);

    /**
     * Получение данных связанных с пользователем о всех темах для изучения
     * @param uuid
     *  УИД пользователя
     * @return
     *  Список тем с признаками learn и teach
     */
    Option<List<TopicRowEntity>,TopicRowDataError> getAllByUserUIDOpt(UUID uuid);

    /**
     * Получение списока тем, которые пользователь выделил признаком Learn
     * @param uuid
     *  uid пользователя
     * @return
     *  Список тем
     * @throws TopicRowDataError
     *  Ошибка доступа к данным
     */
    Option<Set<TopicEntity>,TopicRowDataError> getLearnTopicsByUserUID(UUID uuid);

    /**
     * Получение списока тем, которые пользователь выделил признаком Teach
     * @param uid
     *  uid пользователя
     * @return
     *  Список тем
     * @throws TopicRowDataError
     *  Ошибка доступа к данным
     */
    Option<Set<TopicEntity>,TopicRowDataError> getTeachTopicsByUserUID(UUID uid);

}
