package ru.alcereo.pairlearning.Service.TopicService;


import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.User;

public interface TopicRowChanger {

    Option<Boolean, TopicServiceException> setPredicate(TopicRowsDAO topicRows, User user);

    TopicRowChanger setPredicateValue(boolean predicate);

    TopicRowChanger setTopicId(Long id);

}
