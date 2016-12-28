package ru.alcereo.pairlearning.DAO;


import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.DAO.models.Topic;
import ru.alcereo.pairlearning.DAO.models.User;

import java.util.List;
import java.util.Set;

public interface TopicRowsDAO {

    void setLearnPredicate(Long id, User userModel, boolean predicate) throws TopicRowDataError;

    void setTeachPredicate(Long id, User userModel, boolean predicate) throws TopicRowDataError;

    List<TopicRow> getAllByUser(User userModel) throws TopicRowDataError;

    Set<Topic> getLearnTopicsByUser(User user) throws TopicRowDataError;

    Set<Topic> getTeachTopicsByUser(User user) throws TopicRowDataError;

}
