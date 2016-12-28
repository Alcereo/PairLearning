package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.DAO.models.User;

import java.util.List;
import java.util.UUID;


public interface UsersDAO {

    List<User> getAll() throws UserDataError;
    User findByUid(UUID uuid) throws UserDataError;
    User findByLogin(String login) throws UserDataError;
    boolean addUser(User user) throws UserDataError;
    boolean deleteUser(User user) throws UserDataError;
    User makeActive(User user) throws UserDataError;

}
