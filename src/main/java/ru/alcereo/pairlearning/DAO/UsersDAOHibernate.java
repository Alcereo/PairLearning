package ru.alcereo.pairlearning.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.Entities.UserEntity;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class UsersDAOHibernate implements UsersDAO {

    private static final Logger log = LoggerFactory.getLogger(UsersDAOHibernate.class);

    private SessionFactory sessionFactory;

    public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
        this.sessionFactory = hibernateSessionFactory;
    }

    @Override
    public Option<UserEntity, UserDataError> findByUidOpt(UUID uuid) {
        return sessionedAndOptionedAction(session ->
                        session.get(UserEntity.class, uuid)
        );
    }

    @Override
    public Option<UserEntity, UserDataError> findByLoginOpt(String login) {
        return sessionedAndOptionedAction(session ->
                        session
                        .createQuery("from UserEntity where login=:login", UserEntity.class)
                        .setParameter("login", login)
                        .uniqueResultOptional().orElseGet(null)
        );
    }

    @Override
    public Option<Boolean, UserDataError> loginInUse(String login) {
        return sessionedAndOptionedAction(session ->
                session.get(UserEntity.class, "login = :login")!=null
        );
    }

    @Override
    public Option<Boolean, UserDataError> addUserOpt(UserEntity user) {
        return sessionedAndOptionedAction(session -> {
                    session.beginTransaction();
                    session.save(Objects.requireNonNull(user));
                    session.getTransaction().commit();
                    return true;
                }
        );
    }

    @Override
    public Option<Boolean, UserDataError> save(UserEntity user) {
        return sessionedAndOptionedAction(session -> {
            session.beginTransaction();
            session.update(Objects.requireNonNull(user));
            session.getTransaction().commit();
            return true;
        });
    }

    private <RESULT> Option<RESULT,UserDataError> sessionedAndOptionedAction(Function<Session,RESULT> function){
        try(Session session = sessionFactory.openSession()) {
            return Option.asOption(() -> function.apply(session))
                    .wrapException(this::userDataErrorWrapper);
        }
    }

    UserDataError userDataErrorWrapper(Throwable cause){
        return new UserDataError("Ошибка доступа к данным", cause);
    }

}
