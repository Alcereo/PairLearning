package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.User;
import ru.alcereo.pairlearning.DAO.UsersDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UsersDAOMock implements UsersDAO {

    private static List<User> users = new ArrayList<>();

    static {
        users.add(new User(UUID.randomUUID(),"Ivan1", "40bd001563085fc35165329ea1ff5c5ecbdbbeef","Иван1"));
        users.add(new User(UUID.randomUUID(),"Ivan2","40bd001563085fc35165329ea1ff5c5ecbdbbeef","Иван2"));
        users.add(new User(UUID.randomUUID(),"Ivan3","40bd001563085fc35165329ea1ff5c5ecbdbbeef","Иван3"));
        users.add(new User(UUID.randomUUID(),"Ivan4","40bd001563085fc35165329ea1ff5c5ecbdbbeef","Иван4"));
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
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void deleteUser(User user) {

    }
}
