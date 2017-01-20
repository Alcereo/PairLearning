package ru.alcereo.pairlearning.DAO.springData;

import org.springframework.beans.factory.annotation.Autowired;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.Entities.UserEntity;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;

import java.util.UUID;


public class UsersDAOSpringData implements UsersDAO {

    private UserEntityRepository repository;

    @Autowired
    public void setRepository(UserEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Option<UserEntity, UserDataError> findByUidOpt(UUID uuid) {
        return Option.asOption(() -> repository.findOne(uuid));
    }

    @Override
    public Option<UserEntity, UserDataError> findByLoginOpt(String login) {
        return Option.asOption(() -> repository.findByLogin(login).get(0));
    }

    @Override
    public Option<Boolean, UserDataError> loginInUse(String login) {
        return null;
    }

    @Override
    public Option<Boolean, UserDataError> addUserOpt(UserEntity user) {
        return null;
    }

    @Override
    public Option<Boolean, UserDataError> save(UserEntity user) {
        return null;
    }

}
