package ru.alcereo.pairlearning.DAO;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import ru.alcereo.pairlearning.DAO.exceptions.SessionDataError;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.Assert.*;


public class SessionDAOPGTest {

    private static DataSource ds;

    @BeforeClass
    public static void prepareDataSource(){
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setServerName("localhost");
        source.setDatabaseName("TestBase");
        source.setUser("postgres");
        source.setPassword("");
        ds = source;
    }

    @Before
    public void prepareTables(){

        String createUsersTableQuery =
                "CREATE TABLE users" +
                "(" +
                "    uid UUID PRIMARY KEY NOT NULL," +
                "    login VARCHAR(30) NOT NULL," +
                "    passwordhash VARCHAR(64) NOT NULL," +
                "    email VARCHAR(60)," +
                "    activae BOOLEAN DEFAULT false," +
                "    name TEXT NOT NULL" +
                "); " +
                "CREATE UNIQUE INDEX users_login_uindex ON users (login);";

        String insertUsersQuery =
                "INSERT INTO users VALUES ('11111111-1111-1111-1111-111111111111','user1', '123', 'mail', true, 'user1')," +
                        "('11111111-1111-1111-1111-111111111112','user2', '123', 'mail', true, 'user2')," +
                        "('11111111-1111-1111-1111-111111111113','user3', '123', 'mail', true, 'user3')";
        
        String createSessionsTableQuery =
                "CREATE TABLE sessions" +
                "(" +
                "    id VARCHAR(100) PRIMARY KEY NOT NULL," +
                "    user_uid UUID NOT NULL," +
                "    CONSTRAINT sessions_users_uid_fk FOREIGN KEY (user_uid) REFERENCES users (uid)" +
                ");";
        
        String insertSessionsQuery =
                "INSERT INTO sessions VALUES ('session1','11111111-1111-1111-1111-111111111111')," +
                        "('session2','11111111-1111-1111-1111-111111111112')," +
                        "('session3','11111111-1111-1111-1111-111111111113')";;
        
        try(
                Connection conn = ds.getConnection();
                Statement st = conn.createStatement();
        ){

            st.executeUpdate(createUsersTableQuery);
            st.executeUpdate(insertUsersQuery);

            st.executeUpdate(createSessionsTableQuery);
            st.executeUpdate(insertSessionsQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @After
    public void dropTables(){
        String dropSessionsTable = "DROP TABLE sessions";
        String dropUsersTable = "DROP TABLE users";

        try(
                Connection conn = ds.getConnection();
                Statement st = conn.createStatement();
        ){
            st.executeUpdate(dropSessionsTable);
            st.executeUpdate(dropUsersTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void getSessionById() throws Exception {

        SessionDAOPG sessionDAOPG = new SessionDAOPG();
        sessionDAOPG.setDataSource(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        assertEquals(
                "Ошибка поиска сессии",
                sessionDAOPG.getSessionById("session1"),
                new Session("session1", user)
                );

        assertEquals(
                "Ошибка поиска сессии",
                sessionDAOPG.getSessionById(null),
                null
        );

    }

    @Test
    public void getSessionByUser() throws Exception {

        SessionDAOPG sessionDAOPG = new SessionDAOPG();
        sessionDAOPG.setDataSource(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        assertEquals(
                "Ошибка поиска сессии",
                sessionDAOPG.getSessionByUser(user),
                new Session("session1", user)
        );

        assertEquals(
                "Ошибка поиска сессии",
                sessionDAOPG.getSessionByUser(null),
                null
        );
    }

    @Test
    public void insertOrUpdateSession() throws Exception {

        SessionDAOPG sessionDAOPG = new SessionDAOPG();
        sessionDAOPG.setDataSource(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        assertEquals(
                "Ошибка изменения сессии",
                sessionDAOPG.insertOrUpdateSession(
                        new Session(
                                "session6",
                                user)
                ),
                true
        );

        assertEquals(
                "Ошибка поиска сессии",
                sessionDAOPG.getSessionByUser(user),
                new Session(
                        "session6",
                        user)
        );

        assertFalse(
                "Ошибка изменения сессии",
                sessionDAOPG.insertOrUpdateSession(
                        new Session(
                                "session6",
                                user)
                )
        );

    }

    @Test(expected = NullPointerException.class)
    public void insertOrUpdateNull() throws SessionDataError {

        SessionDAOPG sessionDAOPG = new SessionDAOPG();
        sessionDAOPG.setDataSource(ds);

        sessionDAOPG.insertOrUpdateSession(null);

    }

    @Test
    public void deleteSessionById() throws Exception {

        SessionDAOPG sessionDAOPG = new SessionDAOPG();
        sessionDAOPG.setDataSource(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        assertTrue(
                "Ошибка удаления сессии",
                sessionDAOPG.deleteSessionById("session1")
        );

        assertEquals(
                "Наидена удаленная сессия",
                sessionDAOPG.getSessionById("session1"),
                null);

        assertFalse(
                "Показывает удаление пустого id",
                sessionDAOPG.deleteSessionById(null)
        );

        assertFalse(
                "Показал удаление уже удаленной давно",
                sessionDAOPG.deleteSessionById("session1")
        );

    }

}