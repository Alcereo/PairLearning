package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.models.User;

import java.util.List;
import java.util.UUID;


public interface UsersDAO {

    List<User> getAll();
    User findByUid(UUID uuid);
    User findByLogin(String login);
    boolean addUser(User user);
    boolean deleteUser(User user);
    User makeActive(User user);

}
