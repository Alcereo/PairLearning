package ru.alcereo.pairlearning.DAO;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import ru.alcereo.pairlearning.DAO.exceptions.UserDataError;
import ru.alcereo.pairlearning.DAO.models.User;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.Assert.*;


public class UsersDAOPGTest {

    private static DataSource ds;

    @BeforeClass
    public static void prepareDataSource(){
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setServerName("localhost");
        source.setDatabaseName("TestBase");
        source.setUser("postgres");
        source.setPassword("");
        UsersDAOPGTest.ds = source;
    }

    @Before
    public void prepareTables(){

        String createTableQuery = "CREATE TABLE users" +
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

        try(
                Connection conn = ds.getConnection();
                Statement st = conn.createStatement();
                ){
            
            st.executeUpdate(createTableQuery);

            st.executeUpdate(insertUsersQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @After
    public void dropTables(){
        String createTableQuery = "DROP TABLE users";

        try(
                Connection conn = ds.getConnection();
                Statement st = conn.createStatement();
        ){

            st.executeUpdate(createTableQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAll() throws Exception {

        UsersDAOPG usersDAO = new UsersDAOPG();
        usersDAO.setDS(ds);

        assertEquals(
                "Не верное количество строк",
                usersDAO.getAll().size(),
                3
        );

    }

    @Test
    public void findByUid() throws Exception {

        UsersDAOPG usersDAO = new UsersDAOPG();
        usersDAO.setDS(ds);

        assertEquals(
                "Наиден не тот пользователь",
                usersDAO.findByUid(UUID.fromString("11111111-1111-1111-1111-111111111111")),
                new User(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "user1",
                        "123",
                        "user1",
                        "mail",
                        true)
        );

        assertEquals(
                "Наиден пользователь по некорректному запросу",
                usersDAO.findByUid(UUID.fromString("11111111-1111-1111-1111-11111110000")),
                null
        );

        assertEquals(
                "Наиден пользователь по некорректному запросу",
                usersDAO.findByUid(null),
                null
        );

    }

    @Test
    public void findByLogin() throws Exception {

        UsersDAOPG usersDAO = new UsersDAOPG();
        usersDAO.setDS(ds);

        assertEquals(
                "Наиден не тот пользователь",
                usersDAO.findByLogin("user1"),
                new User(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "user1",
                        "123",
                        "user1",
                        "mail",
                        true)
        );

        assertEquals(
                "Наиден пользователь по некорректному запросу",
                usersDAO.findByLogin("user5"),
                null
        );

        assertEquals(
                "Наиден пользователь по некорректному запросу",
                usersDAO.findByLogin(null),
                null
        );
    }

    @Test
    public void addUser() throws UserDataError {

        UsersDAOPG usersDAO = new UsersDAOPG();
        usersDAO.setDS(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111118888"),
                "user5",
                "123",
                "user5",
                "mail",
                true);

        assertTrue(
                usersDAO.addUser(user)
        );

        assertEquals(
                "Не создался пользователь",
                usersDAO.findByUid(user.getUid()),
                user
        );

    }

    @Test(expected = UserDataError.class)
    public void addUserDuble() throws UserDataError {

        UsersDAOPG usersDAO = new UsersDAOPG();
        usersDAO.setDS(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111118888"),
                "user5",
                "123",
                "user5",
                "mail",
                true);


        usersDAO.addUser(user);

        usersDAO.addUser(user);

    }

    @Test(expected = NullPointerException.class)
    public void addNullUser() throws UserDataError {
        UsersDAOPG usersDAO = new UsersDAOPG();
        usersDAO.setDS(ds);
        usersDAO.addUser(null);
    }

    @Test(expected = UserDataError.class)
    public void addUserLoginUses() throws UserDataError {
        UsersDAOPG usersDAO = new UsersDAOPG();
        usersDAO.setDS(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111118888"),
                "user1",
                "123",
                "user5",
                "mail",
                true);

        usersDAO.addUser(user);
    }

    @Test
    public void deleteUser() throws Exception {
        UsersDAOPG usersDAO = new UsersDAOPG();
        usersDAO.setDS(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        assertTrue(
                "Не удалился пользователь",
                usersDAO.deleteUser(user)
        );

        assertEquals(
                "Не удалился пользователь",
                usersDAO.getAll().size(),
                2
        );

        user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111118888"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        assertFalse("Признак удаления некосистемных данных",
                usersDAO.deleteUser(user));

    }

    @Test
    public void makeActive() throws Exception {

        UsersDAOPG usersDAO = new UsersDAOPG();
        usersDAO.setDS(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        usersDAO.makeActive(user);

        assertEquals(
                "Не установился признак активности",
                usersDAO.findByUid(user.getUid()),
                user.makeActive()
        );

    }

}