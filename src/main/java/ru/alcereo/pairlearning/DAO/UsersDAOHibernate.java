package ru.alcereo.pairlearning.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.Entities.UserEntity;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.Service.models.User;

import java.util.List;
import java.util.UUID;

/**
 * Created by alcereo on 16.01.17.
 */
public class UsersDAOHibernate implements UsersDAO {

    private static final Logger log = LoggerFactory.getLogger(UsersDAOHibernate.class);

    private SessionFactory sessionFactory;

    public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
        this.sessionFactory = hibernateSessionFactory;
    }

    @Override
    public List<User> getAll() throws UserDataError {
        return null;
    }

    @Override
    public User findByUid(UUID uuid) throws UserDataError {
        return null;
    }

    @Override
    public User findByLogin(String login) throws UserDataError {
        return null;
    }

    @Override
    public Option<UserEntity, UserDataError> findByLoginOpt(String login) {
        try(Session session = sessionFactory.openSession()){
            return Option.asOption(
                    session
                    .createQuery("from UserEntity where login=:login", UserEntity.class)
                    .setParameter("login", login)
                    .uniqueResultOptional().orElseGet(null)
            );
        }
    }

    @Override
    public Option<Boolean, UserDataError> loginInUse(String login) {
        return null;
    }

    @Override
    public boolean addUser(User user) throws UserDataError {
        return false;
    }

    @Override
    public Option<Boolean, UserDataError> addUser_Opt(User user) {
        return null;
    }

    @Override
    public boolean deleteUser(User user) throws UserDataError {
        return false;
    }

    @Override
    public User makeActive(User user) throws UserDataError {
        return null;
    }
}
