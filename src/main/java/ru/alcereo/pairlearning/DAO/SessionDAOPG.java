package ru.alcereo.pairlearning.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.Service.models.Session;
import ru.alcereo.pairlearning.Service.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;


/**
 * Объект доступа к данным сессий из таблицы PostgreSQL
 */
public class SessionDAOPG implements SessionDAO{

    private static final Logger log = LoggerFactory.getLogger(SessionDAOPG.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    private User userFromResultSet(ResultSet resultSet) throws SQLException {

        return new User(
                UUID.fromString(resultSet.getString("uid")),
                resultSet.getString("login"),
                resultSet.getString("passwordhash"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getBoolean("activae")
        );
    }

    private Session sessionFromResultSet(ResultSet resultSet) throws SQLException {

        return new Session(
                resultSet.getString("id"),
                userFromResultSet(resultSet)
        );
    }


    public Session getSessionById(String id) throws SessionDataError {

        Session result=null;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * FROM sessions LEFT JOIN users ON sessions.user_uid=users.uid WHERE sessions.id=?")
        ){

            st.setString(1,id);

            try(ResultSet resultSet = st.executeQuery()){

                while (resultSet.next())
                    result = sessionFromResultSet(resultSet);
            }

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new SessionDataError("Ошибка обращения к данным сессий",e);
        }

        return result;
    }

    @Override
    public Option<Session, SessionDataError> getSessionOptById(String SessionId){

        Option<Session, SessionDataError> result=Option.NONE;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * FROM sessions LEFT JOIN users ON sessions.user_uid=users.uid WHERE sessions.id=?")
        ){

            st.setString(1,SessionId);

            try(ResultSet resultSet = st.executeQuery()){

                while (resultSet.next())
                    result = Option.asOption(sessionFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            result = Option.asException(new SessionDataError("Ошибка обращения к данным сессий",e));
        }

        return result;
    }

    public Session getSessionByUser(User user) throws SessionDataError {
        Session result=null;

        if (user!=null) {
            try (
                    Connection conn = dataSource.getConnection();
                    PreparedStatement st = conn.prepareStatement(
                            "SELECT * FROM sessions LEFT JOIN users ON sessions.user_uid=users.uid WHERE users.uid=?")
            ) {

                st.setObject(1, user.getUid());

                try (ResultSet resultSet = st.executeQuery()) {
                    while (resultSet.next())
                        result = sessionFromResultSet(resultSet);
                }

            } catch (SQLException e) {
                log.warn(e.getLocalizedMessage());
                throw new SessionDataError("Ошибка обращения к данным сессий", e);
            }
        }

        return result;
    }

    public boolean insertOrUpdateSession(Session session) throws SessionDataError {

        Objects.requireNonNull(session, "Передана пустая сессия!");

        String upsertQuery =
                "WITH upsert AS" +
                "(UPDATE sessions" +
                "  SET user_uid = ?" +
                "  WHERE id = ?" +
                "  RETURNING *)" +
                "INSERT INTO sessions (id, user_uid)" +
                "  SELECT" +
                "    ?," +
                "    ?" +
                "  WHERE NOT EXISTS" +
                "  (SELECT *" +
                "   FROM upsert)";

        boolean result;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(upsertQuery)
        ){

            st.setObject(1, session.getUser().getUid());
            st.setObject(4, session.getUser().getUid());

            st.setObject(2, session.getSessionId());
            st.setObject(3, session.getSessionId());

            result = st.executeUpdate()==1;

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new SessionDataError("Ошибка обращения к данным сессий",e);
        }

        return result;

    }

    public boolean deleteSessionById(String sessionId) throws SessionDataError {

        boolean result;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement("DELETE FROM sessions WHERE id = ?")
        ){

            st.setString(1, sessionId);

            result = st.executeUpdate()==1;

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new SessionDataError("Ошибка обращения к данным сессий",e);
        }

        return result;

    }

}
