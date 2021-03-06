package ru.alcereo.pairlearning.Service.TopicService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.Service.EntityMapper;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.TopicRow;
import ru.alcereo.pairlearning.Service.models.TopicRowFront;
import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.models.UserFront;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Сервис работы с темами для изучения
 */
@Service
public class TopicServiceImpl implements TopicService {

    private static final Logger log = LoggerFactory.getLogger(TopicServiceImpl.class);

    private UsersDAO users;
    private TopicRowsDAO topicRows;
    private EntityMapper entityMapper;

    private List<TopicRowChanger> topicsList;

    @Autowired
    public void setTopicsList(List<TopicRowChanger> topicsList) {
        this.topicsList = topicsList;
    }

    @Autowired
    public void setEntityMapper(EntityMapper entityMapper){
        this.entityMapper = entityMapper;
    }

    @Autowired
    public void setUsersDAO(UsersDAO usersDAO) {
        this.users = usersDAO;
    }

    @Autowired
    public void setTopicRows(TopicRowsDAO topicRows) {
        this.topicRows = topicRows;
    }

//     * -------------------------------------------------------- *
//     *                     ОСНОВНЫЕ ФУНКЦИИ                     *
//     * -------------------------------------------------------- *

    /**
     * Получение всех тем с признаками для данного пользователя
     * @param user_n
     *  Пользователь
     * @return
     *  Список строк таблицы с темами и признаками для данного пользователя
     */
    @Transactional
    public Option<List<TopicRowFront>,TopicServiceException> getUserTopicOpt(UserFront user_n) {

        return Option.asNotNullWithExceptionOption(user_n)
                .flatMap(
                        user ->
                           users
                           .findByLoginOpt(user.getLogin())
                           .map(entityMapper::wrapToUser)
                           .flatMap(
                                   userModel -> topicRows.getAllByUserUIDOpt(userModel.getUid())
                           ).map(
                               topicRowEntityList ->
                                       topicRowEntityList
                                       .stream()
                                       .map(topicRowEntity -> (TopicRowFront)entityMapper.wrapToTopicRow(topicRowEntity))
                                       .collect(Collectors.toList())
                                )
                )
                .wrapException(TopicServiceImpl::topicServiceExceptionWrapper);
    }

    /**
     * Изменение выбранного признака для темы
     * @param data_n
     *  Данные с информацией об изменениях
     */
    @Override
    public Option<Boolean, TopicServiceException> setTopicRow(TopicChangeData data_n){
        return Option.asNotNullWithExceptionOption(data_n)
        .flatMap(data ->
           Option.asOption(() ->
                  topicsList
                  .stream()
                  .filter(topicRowChanger -> topicRowChanger.canSubmitThisData(data))
                  .findFirst()
                  .get()
           ).flatMap(topicRowChanger -> topicRowChanger.setPredicate(data))
        ).wrapException(TopicServiceImpl::topicServiceExceptionWrapper);
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
    @Override
    public Option<Boolean, TopicServiceException> usersHaveIntersectionTopics(UserFront user1_n, UserFront user2_n){

        return Option.asNotNullWithExceptionOption(user1_n)
        .flatMap(user1 ->
        Option.asNotNullWithExceptionOption(user2_n)
        .flatMap(user2 -> topicRows
            .getLearnTopicsByUserUID(user1.getUid())
            .flatMap(
                    user1Set -> topicRows
                            .getLearnTopicsByUserUID(user2.getUid())
                            .map(user1Set::retainAll)
            ).flatMap(
                    haveFirstRetain ->
                        haveFirstRetain ?
                            topicRows
                            .getTeachTopicsByUserUID(user1.getUid())
                            .flatMap(userSet1 ->
                                    topicRows.getLearnTopicsByUserUID(user2.getUid())
                                    .map(userSet1::retainAll))
                                :
                            Option.asOption(false)
            )
        ))
        .wrapException(TopicServiceImpl::topicServiceExceptionWrapper);
    }

//     * -------------------------------------------------------- *
//     *                       СЛУЖЕБНЫЕ                          *
//     * -------------------------------------------------------- *

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
