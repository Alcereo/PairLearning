package ru.alcereo.pairlearning.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.*;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.DAO.models.Topic;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.TopicPredicateSide;
import ru.alcereo.pairlearning.Service.models.TopicRowFront;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Сервис работы с темами для изучения
 */
public class TopicService {

    private static final Logger log = LoggerFactory.getLogger(TopicService.class);

    private static final UsersDAO users = new UsersDAOPG();
    private static TopicRowsDAO topicRows = new TopicRowsDAOPG();


    /**
     * Получение всех тем с признаками для данного пользователя
     * @param user
     *  Пользователь
     * @return
     *  Список строк таблицы с темами и признаками для данного пользователя
     */
    public static List<TopicRowFront> getUserTopic(UserFront user) throws TopicServiceException {

        List<TopicRowFront> result = new ArrayList<>();

        if (user == null)
            throw new TopicServiceException(
                    "Ошибка сервиса тем. Некорректные данные. Не заполнен пользователь.",
                    new IllegalArgumentException("user == null")
            );

        try{
            User userModel = users.findByLogin(user.getLogin());

            if (userModel != null)
                for (TopicRow topicRow : topicRows.getAllByUser(userModel))
                    result.add(topicRow);
        } catch (UserDataError | TopicRowDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new TopicServiceException(
                    "Ошибка сервиса тем изучения. Ошибка доступа к данным.",
                    e);
        }

        return result;

    }


    /**
     * Изменение признака для темы и выбранного пользователя
     * @param userFront
     *  Пользователь
     * @param id
     *  Идентификатор темы
     * @param side
     *  Признак того, какой признак устанавливается
     * @param predicate
     *  Значение предиката
     */
    public static void setTopicRow(UserFront userFront, Long id, TopicPredicateSide side, boolean predicate) throws TopicServiceException {

        if (userFront == null)
            throw new TopicServiceException(
                    "Ошибка сервиса тем. Некорректные данные. Не заполнен пользователь.",
                    new IllegalArgumentException("userFront == null")
            );

        if (id == null)
            throw new TopicServiceException(
                    "Ошибка сервиса тем. Некорректные данные. Не заполнен идентификатор темы.",
                    new IllegalArgumentException("id == null")
            );

        if (side == null)
            throw new TopicServiceException(
                    "Ошибка сервиса тем. Некорректные данные. Не заполнен признак.",
                    new IllegalArgumentException("side == null")
            );


        try {
            User user = users.findByUid(userFront.getUid());

            switch (side) {
                case LEARN:
                    log.debug("Меняем LEARN на {} у id:{}", predicate, id);
                    topicRows.setLearnPredicate(id, user, predicate);

                    break;
                case TEACH:
                    log.debug("Меняем TEACH на {} у id:{}", predicate, id);
                    topicRows.setTeachPredicate(id, user, predicate);
                    break;
            }
        } catch (UserDataError | TopicRowDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new TopicServiceException("Ошибка сервиса тем. Ошибка обращения к данным.", e);
        }

    }


    /**
     * Проверка на то, что пользователей есть пересечение в интересах по темам,
     * конкретно, что:
     * <ul>
     *     <li>Пользователей в списке не больше чем 2</li>
     *     <li>У одно из пользователей в списке выбраны темы, для изучения и преподавания, которые у второго выбраны
     *     зеркально соответственно для преподавания и изучения</li>
     * </ul>
     * @param usersList
     *  Список пользователей для проверки
     * @return
     *  true - если:
     *  <ul>
     *     <li>Пользователей в списке не больше чем 2</li>
     *     <li>У одно из пользователей в списке выбраны темы, для изучения и преподавания, которые у второго выбраны
     *     зеркально соответственно для преподавания и изучения</li>
     * </ul>
     * false - если обратное
     *
     */
    public static boolean usersHaveIntersectionTopics(List<UserFront> usersList) throws TopicServiceException {

        boolean result=false;

        log.debug("Определяем возможность входа для: {}", usersList);

        if (usersList.size() < 2)
            result = true;

        if (usersList.size()==2){

            try {


                User user1 = users.findByLogin(usersList.get(0).getLogin());
                User user2 = users.findByLogin(usersList.get(1).getLogin());

                Set<Topic> userSet1 = topicRows.getLearnTopicsByUser(user1);
                Set<Topic> userSet2 = topicRows.getTeachTopicsByUser(user2);

                userSet1.retainAll(userSet2);

                log.debug("Количество пересечений 1: {}", userSet1.size());

                if (userSet1.size() > 0) {

                    userSet1 = topicRows.getTeachTopicsByUser(user1);
                    userSet2 = topicRows.getLearnTopicsByUser(user2);

                    userSet1.retainAll(userSet2);

                    log.debug("Количество пересечений 2: {}", userSet1.size());

                    if (userSet1.size() > 0)
                        result = true;
                }

            } catch (UserDataError | TopicRowDataError e) {
                log.warn(e.getLocalizedMessage());
                throw new TopicServiceException(
                        "Ошибка сервиса тем изучения. Ошибка обращения к данным",
                        e);
            }

        }

        return result;
    }

}
