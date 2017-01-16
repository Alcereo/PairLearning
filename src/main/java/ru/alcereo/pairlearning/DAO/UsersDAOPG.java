package ru.alcereo.pairlearning.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.Entities.UserEntity;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.Service.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UsersDAOPG implements UsersDAO {

    private static final Logger log = LoggerFactory.getLogger(UsersDAOPG.class);

    private DataSource dataSource;

    public void setDataSource(DataSource ds){
        this.dataSource = ds;
    }

    private static final String getAllquery=
            "SELECT * FROM users";

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



    @Override
    public List<User> getAll() throws UserDataError {

        List<User> result = new ArrayList<>();

        try(
                Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();
                ResultSet resultSet = st.executeQuery(getAllquery);
                ){

            while (resultSet.next())
                result.add(userFromResultSet(resultSet));

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new UserDataError("Ошибка обращения к данным по пользователям", e);
        }

        return result;
    }

    @Override
    public User findByUid(UUID uuid) throws UserDataError {
        User result = null;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * FROM users WHERE uid=?");
        ){

            st.setObject(1,uuid);

            try(ResultSet resultSet = st.executeQuery();){

                while (resultSet.next())
                    result = userFromResultSet(resultSet);
            }

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new UserDataError("Ошибка обращения к данным по пользователям", e);
        }

        return result;
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
                        "SELECT * FROM users WHERE login=?");
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
    public User findByLogin(String login) throws UserDataError {

        User result = null;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * FROM users WHERE login=?");
        ){

            st.setString(1, login);

            try(ResultSet resultSet = st.executeQuery();){

                while (resultSet.next())
                    result = userFromResultSet(resultSet);
            }

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new UserDataError("Ошибка обращения к данным по пользователям", e);
        }

        return result;
    }

    @Override
    public boolean addUser(User user) throws UserDataError {

        Objects.requireNonNull(user, "Передан пользователь с сылкой null");

        boolean result = false;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(addQuery);
        ){


            st.setObject(1,user.getUid());
            st.setString(2,user.getLogin());
            st.setString(3,user.getPasswordHash());
            st.setString(4,user.getEmail());
            st.setBoolean(5,user.isActive());
            st.setString(6,user.getName());

            result = st.executeUpdate()==1;
        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new UserDataError("Ошибка обращения к данным по пользователям", e);
        }

        return result;

    }

    @Override
    public Option<Boolean, UserDataError> addUser_Opt(User user_n) {

        return Option.asOption(() -> Objects.requireNonNull(user_n, "Передан пользователь с сылкой null"))
                .map(user -> {
                    try (
                            Connection conn = dataSource.getConnection();
                            PreparedStatement st = conn.prepareStatement(addQuery);
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
    public boolean deleteUser(User user) throws UserDataError {

        boolean result = false;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(deleteQuery);
        ){

            st.setObject(1,user.getUid());
            result = st.executeUpdate()==1;

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new UserDataError("Ошибка обращения к данным по пользователям", e);
        }

        return result;
    }

    @Override
    public User makeActive(User user) throws UserDataError {
        User result=null;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE users SET activae=TRUE WHERE uid=?"
                )
        ){

            st.setObject(1,user.getUid());

            if (st.executeUpdate()==1)
                result = user.makeActive();

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new UserDataError("Ошибка обращения к данным по пользователям", e);
        }

        return result;
    }

    private static UserDataError userDataErrorWrapper(Throwable cause) {
        log.warn(cause.getMessage());

        return new UserDataError(
                "Ошибка доступа к данным пользователей",
                cause);
    }


}
