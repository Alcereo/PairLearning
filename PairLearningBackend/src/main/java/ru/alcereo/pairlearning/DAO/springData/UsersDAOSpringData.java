package ru.alcereo.pairlearning.DAO.springData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.alcereo.exoption.Func;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.Entities.UserEntity;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;

import java.util.UUID;

@Primary
@Repository
public class UsersDAOSpringData implements UsersDAO {
    private static final Logger log = LoggerFactory.getLogger(UsersDAOSpringData.class);

    private UserEntityRepository repository;

    @Autowired
    public void setRepository(UserEntityRepository repository) {
        this.repository = repository;
    }


//     * -------------------------------------------------------- *
//     *                     ОСНОВНЫЕ ФУНКЦИИ                     *
//     * -------------------------------------------------------- *

    @Override
    public Option<UserEntity, UserDataError> findByUidOpt(UUID uuid) {
        return optionedRepositoryAction(repository ->
                repository.findOne(uuid)
        );
    }

    @Override
    public Option<UserEntity, UserDataError> findByLoginOpt(String login) {
        return optionedRepositoryAction(repository ->
                repository
                .findByLogin(login)
                .get(0)
        );
    }

    @Override
    public Option<Boolean, UserDataError> loginInUse(String login) {
        return optionedRepositoryAction(repository ->
                repository
                .findByLogin(login)
                .size()>0
        );
    }

    @Override
    public Option<Boolean, UserDataError> addUserOpt(UserEntity user) {
        return optionedRepositoryAction(repository ->
                repository
                .save(user)!=null
        );
    }

    @Override
    public Option<Boolean, UserDataError> save(UserEntity user) {
        return optionedRepositoryAction(repository ->
                repository
                .save(user)!=null
        );
    }


//     * -------------------------------------------------------- *
//     *                       СЛУЖЕБНЫЕ                          *
//     * -------------------------------------------------------- *

    private<RESULT> Option<RESULT, UserDataError>
        optionedRepositoryAction(Func<UserEntityRepository, RESULT,UserDataError> func){
        return Option.asOption(
                () -> func.execute(repository)
        ).wrapException(this::errorWrapper);
    }

    UserDataError errorWrapper(Throwable cause){
        log.error(cause.getLocalizedMessage());
        return new UserDataError("Ошибка получения данных", cause);
    }

}
