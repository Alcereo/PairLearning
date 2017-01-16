package ru.alcereo.pairlearning.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.Entities.TopicRowEntity;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.Topic;
import ru.alcereo.pairlearning.Service.models.TopicRow;
import ru.alcereo.pairlearning.Service.models.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by alcereo on 16.01.17.
 */
public class TopicRowsDAOHibernate implements TopicRowsDAO {

    private SessionFactory sessionFactory;

    public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
        this.sessionFactory = hibernateSessionFactory;
    }

    @Override
    public void setLearnPredicate(Long id, User user, boolean predicate) throws TopicRowDataError {
    }

    @Override
    public void setTeachPredicate(Long id, User user, boolean predicate) throws TopicRowDataError {
    }

    @Override
    public List<TopicRow> getAllByUser(User user) throws TopicRowDataError {
        throw new TopicRowDataError("NOT IMPLEMENTED!!");
    }

    @Override
    public Option<List<TopicRowEntity>,TopicRowDataError> getAllByUserUID(UUID uuid){
        try (Session session = sessionFactory.openSession()){
            return Option.asOption(
                    session
                            .createQuery(
                                    "from TopicRowEntity where user.uid=:uuid",
                                    TopicRowEntity.class
                            )
                            .setParameter("uuid",uuid)
                            .getResultList()
            );
        }
    }

    @Override
    public Set<Topic> getLearnTopicsByUser(User user) throws TopicRowDataError {
        throw new TopicRowDataError("NOT IMPLEMENTED!!");
    }

    @Override
    public Set<Topic> getLearnTopicsByUserUID(UUID uuid) throws TopicRowDataError {
        throw new TopicRowDataError("NOT IMPLEMENTED!!");
    }

    @Override
    public Set<Topic> getTeachTopicsByUser(User user) throws TopicRowDataError {
        throw new TopicRowDataError("NOT IMPLEMENTED!!");
    }

    @Override
    public Set<Topic> getTeachTopicsByUserUID(UUID uid) throws TopicRowDataError {
        throw new TopicRowDataError("NOT IMPLEMENTED!!");
    }
}
