package ru.alcereo.pairlearning.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.exoption.Option;
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


public class TopicRowsDAOHibernate implements TopicRowsDAO {

    private static final Logger log = LoggerFactory.getLogger(TopicRowsDAOHibernate.class);

    private SessionFactory sessionFactory;

    public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
        this.sessionFactory = hibernateSessionFactory;
    }

    @Override
    public Option<Boolean, TopicRowDataError> setLearnPredicate(Long id, User user, boolean predicate) {
        return sessionedAndOptionedAction(session -> {
                    session.beginTransaction();
                    boolean result = session
                            .createQuery(
                                    "update TopicRowEntity set learn=:predicate where " +
                                            "user in (from UserEntity as u where uid=:uid) " +
                                            "and " +
                                            "topic in (from TopicEntity as t where id=:id)"
                            )
                            .setParameter("predicate", predicate)
                            .setParameter("uid", user.getUid())
                            .setParameter("id", id)
                            .executeUpdate() > 0;
                    session.getTransaction().commit();
                    return result;
                }
        );
    }

    @Override
    public Option<Boolean, TopicRowDataError> setTeachPredicate(Long id, User user, boolean predicate) {
        return sessionedAndOptionedAction(session -> {
                    session.beginTransaction();
                    boolean result = session
                            .createQuery(
                                    "update TopicRowEntity set teach=:predicate where " +
                                            "user in (from UserEntity as u where uid=:uid) " +
                                            "and " +
                                            "topic in (from TopicEntity as t where id=:id)"
                            )
                            .setParameter("predicate", predicate)
                            .setParameter("uid", user.getUid())
                            .setParameter("id", id)
                            .executeUpdate() > 0;
                    session.getTransaction().commit();
                    return result;
                }
        );
    }

    @Override
    public Option<List<TopicRowEntity>,TopicRowDataError> getAllByUserUIDOpt(UUID uuid){
        return sessionedAndOptionedAction(session ->
            session
            .createQuery("select tr from TopicRowEntity as tr right join tr.topic as top on tr.user.uid=:uuid order by tr.topic.id", TopicRowEntity.class)
            .setParameter("uuid", Objects.requireNonNull(uuid))
            .getResultList()
        );
    }

    @Override
    public Option<Set<TopicEntity>,TopicRowDataError> getLearnTopicsByUserUID(UUID uid){
        return sessionedAndOptionedAction(session ->
                session.createQuery(
                        "select tre.topic from TopicRowEntity as tre where tre.user.uid=:uid and tre.learn=true",
                        TopicEntity.class
                ).setParameter("uid", uid)
                .list().stream().collect(Collectors.toSet())
        );
    }

    @Override
    public Option<Set<TopicEntity>,TopicRowDataError> getTeachTopicsByUserUID(UUID uid) {
        return sessionedAndOptionedAction(session ->
                session.createQuery(
                        "select tre.topic from TopicRowEntity as tre where tre.user.uid=:uid and tre.teach=true",
                        TopicEntity.class
                ).setParameter("uid", uid)
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
