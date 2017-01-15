package ru.alcereo.pairlearning.Service.TopicService;


import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;

public interface TopicRowChanger {

    void setPredicate(TopicRowsDAO topicRows, User user) throws TopicServiceException;

    TopicRowChanger setPredicateValue(boolean predicate);

    TopicRowChanger setTopicId(Long id);

}
