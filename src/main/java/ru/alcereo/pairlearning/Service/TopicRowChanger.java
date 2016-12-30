package ru.alcereo.pairlearning.Service;


import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.UserFront;

public interface TopicRowChanger {

    void setPredicate(TopicRowsDAO topicRows, User user) throws TopicServiceException;

    void setPredicateValue(boolean predicate);

    void setTopicId(Long id);

}
