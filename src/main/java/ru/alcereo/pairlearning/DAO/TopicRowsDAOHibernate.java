package ru.alcereo.pairlearning.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.Entities.TopicEntity;
import ru.alcereo.pairlearning.DAO.Entities.TopicRowEntity;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.User;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by alcereo on 16.01.17.
 */
public class TopicRowsDAOHibernate implements TopicRowsDAO {

    private static final Logger log = LoggerFactory.getLogger(TopicRowsDAOHibernate.class);

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
    public Option<List<TopicRowEntity>,TopicRowDataError> getAllByUserUIDOpt(UUID uuid){
        return sessionedAndOptionedAction(session ->
            session
            .createQuery(
                    "from TopicRowEntity where user.uid=:uuid",
                    TopicRowEntity.class
            )
            .setParameter("uuid", Objects.requireNonNull(uuid))
            .getResultList()
        );
    }

    @Override
    public Option<Set<TopicEntity>,TopicRowDataError> getLearnTopicsByUserUID(UUID uuid){
        return sessionedAndOptionedAction(session ->
                session.createQuery(
                        "select TopicRowEntity.topic from TopicRowEntity where user.uid=:uuid and learn",
                        TopicEntity.class
                )
                .list().stream().collect(Collectors.toSet())
        );
    }

    @Override
    public Option<Set<TopicEntity>,TopicRowDataError> getTeachTopicsByUserUID(UUID uid) {
        return sessionedAndOptionedAction(session ->
                session.createQuery(
                        "select TopicRowEntity.topic from TopicRowEntity where user.uid=:uuid and teach",
                        TopicEntity.class
                )
                        .list().stream().collect(Collectors.toSet())
        );
    }

    private <RESULT> Option<RESULT,TopicRowDataError> sessionedAndOptionedAction(Function<Session,RESULT> function){
        try(Session session = sessionFactory.openSession()) {
            return Option.asOption(() -> function.apply(session))
                    .wrapException(this::errorWrapper);
        }
    }

    TopicRowDataError errorWrapper(Throwable cause){
        log.error(cause.getLocalizedMessage());
        return new TopicRowDataError("Ошибка доступа к данным", cause);
    }

}
