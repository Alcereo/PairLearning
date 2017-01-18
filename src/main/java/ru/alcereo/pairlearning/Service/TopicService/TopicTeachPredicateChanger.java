package ru.alcereo.pairlearning.Service.TopicService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;


public class TopicTeachPredicateChanger extends AbstractTopicPredicateChanger {

    private static final Logger log = LoggerFactory.getLogger(TopicTeachPredicateChanger.class);

    @Override
    public Option<Boolean, TopicServiceException> setPredicate(TopicRowsDAO topicRows_n, User user_n) {

        return Option.asNotNullWithExceptionOption(topicRows_n)
        .flatMap(topicRows ->
          Option.asNotNullWithExceptionOption(user_n)
        .flatMap(
                  user -> topicRows.setTeachPredicate(id, user, value)
        ))
        .wrapException(this::exceptionWrapper);
    }

    TopicServiceException exceptionWrapper(Throwable cause){
        log.error(cause.getLocalizedMessage());

        if (cause instanceof TopicRowDataError)
            return new TopicServiceException("Ошибка сервиса тем. Ошибка обращения к данным.", cause);

        return new TopicServiceException("Ошибка сервиса тем.", cause);
    }

}
