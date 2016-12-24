package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.User;

import java.util.List;
import java.util.UUID;


public interface UsersDAO {

    List<User> getAll();
    User findByUid(UUID uuid);
    User findByLogin(String login);
    void addUser(User user);
    void deleteUser(User user);

}
