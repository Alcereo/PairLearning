package ru.alcereo.pairlearning.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.Entities.UserEntity;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.Service.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class UsersDAOPG implements UsersDAO {

    private static final Logger log = LoggerFactory.getLogger(UsersDAOPG.class);

    private DataSource dataSource;

    public void setDataSource(DataSource ds){
        this.dataSource = ds;
    }

    private static final String addQuery=
            "INSERT INTO users(" +
            "uid, " +
            "login, " +
            "passwordhash, " +
            "email, " +
            "activae, " +
            "name) " +
            "VALUES (?,?,?,?,?,?)";

    private static final String deleteQuery=
            "DELETE FROM users WHERE uid = ?";


    private User userFromResultSet(ResultSet resultSet) throws SQLException {

        return new User(
                UUID.fromString(resultSet.getString("uid")),
                resultSet.getString("login"),
                resultSet.getString("passwordhash"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getBoolean("active")
        );
    }

    private UserEntity userEntityFromResultSet(ResultSet resultSet) throws SQLException {
        UserEntity userEntity = new UserEntity();
        userEntity.setUid(UUID.fromString(resultSet.getString("uid")));
        userEntity.setPasswordHash(resultSet.getString("passwordhash"));
        userEntity.setName(resultSet.getString("name"));
        userEntity.setEmail(resultSet.getString("email"));
        userEntity.setActive(resultSet.getBoolean("active"));

        return userEntity;
    }

    UserEntity findByUid(UUID uuid) throws UserDataError {
        UserEntity result = null;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * FROM users WHERE uid=?");
        ){

            st.setObject(1,uuid);

            try(ResultSet resultSet = st.executeQuery();){

                while (resultSet.next())
                    result = userEntityFromResultSet(resultSet);
            }

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new UserDataError("Ошибка обращения к данным по пользователям", e);
        }

        return result;
    }

    @Override
    public Option<UserEntity, UserDataError> findByUidOpt(UUID uuid) {
        return Option.asOption(() -> findByUid(uuid));
    }

    @Override
    public Option<UserEntity, UserDataError> findByLoginOpt(String login) {

        Option<UserEntity, UserDataError> result = Option.NONE;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * FROM users WHERE login=?");
        ){

            st.setString(1, login);

            try(ResultSet resultSet = st.executeQuery();){

                while (resultSet.next())
                    result = Option.asOption(userEntityFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            result = Option.asException(
                    new UserDataError(
                            "Ошибка обращения к данным по пользователям",
                            e));
        }

        return result;
    }

    @Override
    public Option<Boolean, UserDataError> loginInUse(String login) {

        Boolean result = false;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * FROM users WHERE login=?")
        ){

            st.setString(1, login);

            try(ResultSet resultSet = st.executeQuery()){
                result = resultSet.next();
            }

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            return Option.asException(
                    new UserDataError(
                            "Ошибка обращения к данным по пользователям",
                            e)
            );
        }

        return Option.asOption(result);
    }

    @Override
    public Option<Boolean, UserDataError> addUserOpt(UserEntity user_n) {

        return Option.asOption(() -> Objects.requireNonNull(user_n, "Передан пользователь с сылкой null"))
                .map(user -> {
                    try (
                            Connection conn = dataSource.getConnection();
                            PreparedStatement st = conn.prepareStatement(addQuery)
                    ) {
                        st.setObject(1, user.getUid());
                        st.setString(2, user.getLogin());
                        st.setString(3, user.getPasswordHash());
                        st.setString(4, user.getEmail());
                        st.setBoolean(5, user.isActive());
                        st.setString(6, user.getName());

                        return st.executeUpdate() == 1;
                    }
                })
                .wrapException(UsersDAOPG::userDataErrorWrapper);
    }

    @Override
    public Option<Boolean, UserDataError> save(UserEntity user) {
        return Option.asException(new UserDataError("NOT IMPLEMENTED!"));
    }

    private static UserDataError userDataErrorWrapper(Throwable cause) {
        log.warn(cause.getMessage());

        return new UserDataError(
                "Ошибка доступа к данным пользователей",
                cause);
    }


}
