package ru.alcereo.pairlearning.Service.TopicService;

import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.Service.models.Topic;
import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
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

    private UsersDAO users;
    private TopicRowsDAO topicRows;
    private MapperFacade mapperFacade;

    public void setUsersDAO(UsersDAO usersDAO) {
        this.users = usersDAO;
    }

    public void setTopicRows(TopicRowsDAO topicRows) {
        this.topicRows = topicRows;
    }

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }


    /**
     * Получение всех тем с признаками для данного пользователя
     * @param user
     *  Пользователь
     * @return
     *  Список строк таблицы с темами и признаками для данного пользователя
     */
    public List<TopicRowFront> getUserTopic(UserFront user) throws TopicServiceException {

        List<TopicRowFront> result = new ArrayList<>();

        if (user == null)
            throw new TopicServiceException(
                    "Ошибка сервиса тем. Некорректные данные. Не заполнен пользователь.",
                    new IllegalArgumentException("user == null")
            );


        users
                .findByLoginOpt(user.getLogin())
                .map(userEntity -> mapperFacade.map(userEntity, User.class))
                .map(
                        userModel -> {
                            topicRows.getAllByUser(userModel).forEach(result::add);
                            return null;
                        })
                .wrapAndTrowException(cause ->
                        new TopicServiceException(
                                "Ошибка сервиса тем изучения. Ошибка доступа к данным.",
                                cause));

        return result;

    }


    /**
     * Изменение выбранного признака для темы
     * @param changer
     *  Объект изменяющий предикат
     * @param userFront
     *  Пользователь
     */
    public void setTopicRow(TopicRowChanger changer, UserFront userFront) throws TopicServiceException {

        try {
            User user = users.findByUid(userFront.getUid());
            changer.setPredicate(topicRows, user);
        } catch (UserDataError e) {
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
     * @param user1_n
     *  Первый пользователь
     * @param user2_n
     *  Второй пользователь
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
    public Option<Boolean, TopicServiceException> usersHaveIntersectionTopics(UserFront user1_n, UserFront user2_n){

        return Option.asNotNullWithExceptionOption(user1_n)
        .flatMap(user1 ->
        Option.asNotNullWithExceptionOption(user2_n)
        .map(user2 ->{

            log.debug("Определяем возможность входа для: {}, {}", user1_n, user2_n);

            boolean OptResult = false;

            Set<Topic> userSet1 = topicRows.getLearnTopicsByUserUID(user1.getUid());
            Set<Topic> userSet2 = topicRows.getLearnTopicsByUserUID(user2.getUid());

            userSet1.retainAll(userSet2);

            log.debug("Количество пересечений 1: {}", userSet1.size());

            if (userSet1.size() > 0) {

                userSet1 = topicRows.getTeachTopicsByUserUID(user1.getUid());
                userSet2 = topicRows.getLearnTopicsByUserUID(user2.getUid());

                userSet1.retainAll(userSet2);

                log.debug("Количество пересечений 2: {}", userSet1.size());

                if (userSet1.size() > 0)
                    OptResult = true;
            }

            return OptResult;

        })).wrapException(TopicService::topicServiceExceptionWrapper);

    }

    private static TopicServiceException topicServiceExceptionWrapper(Throwable cause) {
        log.warn(cause.getMessage());

        if (cause instanceof UserDataError
                || cause instanceof TopicRowDataError)
            return new TopicServiceException(
                    "Ошибка сервиса тем. Ошибка при обращении к данным",
                    cause);

        return new TopicServiceException(
                "Ошибка сервиса тем.",
                cause);
    }

}
