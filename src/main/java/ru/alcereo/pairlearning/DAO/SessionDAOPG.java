package ru.alcereo.pairlearning.DAO;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SessionDAOPG implements SessionDAO{

    private static final Logger log = LoggerFactory.getLogger(SessionDAOPG.class);

    private static DataSource ds;
    static {
        try {
            ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/PairLearning");
        } catch (NamingException e) {
            log.warn("DataSource не был загружен из контекста, используется PGSimpleDataSource");

            PGSimpleDataSource source = new PGSimpleDataSource();
            source.setServerName("localhost");
            source.setDatabaseName("PairLearning");
            source.setUser("postgres");
            source.setPassword("");
            ds = source;
        }
    }


    public static void setDs(DataSource ds) {
        SessionDAOPG.ds = ds;
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


    public Session getSessionById(String id) {

        Session result=null;

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * FROM sessions LEFT JOIN users ON sessions.user_uid=users.uid WHERE sessions.id=?");
        ){

            st.setString(1,id);

            try(ResultSet resultSet = st.executeQuery()){

                while (resultSet.next())
                    result = sessionFromResultSet(resultSet);
            }

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
        }

        return result;
    }

    public Session getSessionByUser(User user) {
        Session result=null;

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * FROM sessions LEFT JOIN users ON sessions.user_uid=users.uid WHERE users.uid=?");
        ){

            st.setObject(1,user.getUid());

            try(ResultSet resultSet = st.executeQuery()){
                while (resultSet.next())
                    result = sessionFromResultSet(resultSet);
            }

        } catch (SQLException e) {
//            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
        }

        return result;
    }

    public boolean insertOrUpdateSession(Session session) {

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

        boolean result=false;

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(upsertQuery);
        ){

            st.setObject(1, session.getUser().getUid());
            st.setObject(4, session.getUser().getUid());

            st.setObject(2, session.getSessionId());
            st.setObject(3, session.getSessionId());

            result = st.executeUpdate()==1;

        } catch (SQLException e) {
            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
        }

        return result;

    }

    public boolean deleteSessionById(String sessionId) {

        boolean result=false;

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement("DELETE FROM sessions WHERE id = ?");
        ){

            st.setString(1, sessionId);

            result = st.executeUpdate()==1;

        } catch (SQLException e) {
            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
        }

        return result;

    }

    public void main(String[] args) {

        SessionDAOPG sessionDAOPG = new SessionDAOPG();

        System.out.println();

    }
}
