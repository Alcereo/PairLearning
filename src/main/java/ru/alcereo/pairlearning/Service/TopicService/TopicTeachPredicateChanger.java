package ru.alcereo.pairlearning.Service.TopicService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;


public class TopicTeachPredicateChanger extends AbstractTopicPredicateChanger {

    private static final Logger log = LoggerFactory.getLogger(TopicTeachPredicateChanger.class);

    @Override
    public void setPredicate(TopicRowsDAO topicRows, User user) throws TopicServiceException {

        if (user == null)
            throw new TopicServiceException(
                    "Ошибка сервиса тем. Некорректные данные. Не заполнен пользователь.",
                    new IllegalArgumentException("userFront == null")
            );

        if (id == -1)
            throw new TopicServiceException(
                    "Ошибка сервиса тем. Некорректные данные. Не заполнен идентификатор темы.",
                    new IllegalArgumentException("id == null")
            );

        try {
            log.debug("Меняем LEARN на {} у id:{}", value, id);
            topicRows.setTeachPredicate(id, user, value);
        } catch (TopicRowDataError e) {
            log.warn(e.getLocalizedMessage());
            throw new TopicServiceException("Ошибка сервиса тем. Ошибка обращения к данным.", e);
        }
    }

}
