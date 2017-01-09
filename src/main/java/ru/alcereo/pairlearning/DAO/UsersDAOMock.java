package ru.alcereo.pairlearning.DAO;

import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.DAO.models.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UsersDAOMock implements UsersDAO {

    private static List<User> users = new ArrayList<>();

    static {
        users.add(new User(
                UUID.randomUUID(),
                "Ivan1",
                "40bd001563085fc35165329ea1ff5c5ecbdbbeef",
                "Иван1",
                "",
                true));

        users.add(new User(
                UUID.randomUUID(),
                "Ivan2",
                "40bd001563085fc35165329ea1ff5c5ecbdbbeef",
                "Иван2",
                "",
                true));
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User findByUid(UUID uuid) {
        User resultUser = null;

        for (User user: users)
            if (user.getUid().equals(uuid))
                resultUser = user;

        return resultUser;
    }

    @Override
    public User findByLogin(String login) {
        User resultUser = null;

        for (User user: users)
            if (user.getLogin().equals(login))
                resultUser = user;

        return resultUser;
    }

    @Override
    public Option<User, UserDataError> findByLoginOpt(String login) {
        return Option.exceptOpt(new UserDataError(new NotImplementedException()));
    }

    @Override
    public Option<Boolean, UserDataError> loginInUse(String login) {
        return Option.exceptOpt(new UserDataError(new NotImplementedException()));
    }

    @Override
    public boolean addUser(User user) {
        users.add(user);
        return true;
    }

    @Override
    public Option<Boolean, UserDataError> addUser_Opt(User user) {
        return Option.exceptOpt(new UserDataError(new NotImplementedException()));
    }

    @Override
    public boolean deleteUser(User user) {
        return false;
    }

    @Override
    public User makeActive(User user) {

        User resultUser = null;

        for (User innerUser: users)
            if (innerUser.equals(user)){
                users.remove(innerUser);
                resultUser = innerUser.makeActive();
                users.add(resultUser);
            }

        return resultUser;

    }

}
