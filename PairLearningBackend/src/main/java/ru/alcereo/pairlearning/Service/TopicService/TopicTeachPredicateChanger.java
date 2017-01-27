package ru.alcereo.pairlearning.Service.TopicService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;

@Component
public class TopicTeachPredicateChanger implements TopicRowChanger {

    private static final Logger log = LoggerFactory.getLogger(TopicTeachPredicateChanger.class);
    private TopicRowsDAO topicRows;

    @Autowired
    public void setTopicRows(TopicRowsDAO topicRows) {
        this.topicRows = topicRows;
    }

    @Override
    public Option<Boolean, TopicServiceException> setPredicate(TopicChangeData data_n) {

        return Option.asNotNullWithExceptionOption(data_n)
        .flatMap(data ->
                topicRows.setTeachPredicate(data.getId(), data.getUser(), data.getValue())
        )
        .wrapException(this::exceptionWrapper);
    }

    TopicServiceException exceptionWrapper(Throwable cause){
        log.error(cause.getLocalizedMessage());

        if (cause instanceof TopicRowDataError)
            return new TopicServiceException("Ошибка сервиса тем. Ошибка обращения к данным.", cause);

        return new TopicServiceException("Ошибка сервиса тем.", cause);
    }

    @Override
    public Boolean canSubmitThisData(TopicChangeData data) {
        return TopicPredicate.TEACH.equals(data.getPredicate());
    }
}
