package ru.alcereo.pairlearning.DAO;


import ru.alcereo.pairlearning.DAO.models.Topic;
import ru.alcereo.pairlearning.DAO.models.User;

import java.util.List;
import java.util.Set;

public interface TopicRowsDAO {

    boolean setLearnPredicate(Long id, User userModel, boolean predicate);

    boolean setTeachPredicate(Long id, User userModel, boolean predicate);

    List<TopicRow> getAllByUser(User userModel);

    Set<Topic> getLearnTopicsByUser(User user);

    Set<Topic> getTeachTopicsByUser(User user);

}
