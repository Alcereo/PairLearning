package ru.alcereo.pairlearning.DAO;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import ru.alcereo.pairlearning.Service.models.Topic;
import ru.alcereo.pairlearning.Service.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TopicRowsDAOPGTest {

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
                        "    activae BOOLEAN DEFAULT FALSE," +
                        "    name TEXT NOT NULL" +
                        "); " +
                        "CREATE UNIQUE INDEX users_login_uindex ON users (login);";

        String insertUsersQuery =
                "INSERT INTO users VALUES " +
                        "('11111111-1111-1111-1111-111111111111','user1', '123', 'mail', TRUE, 'user1')," +
                        "('11111111-1111-1111-1111-111111111112','user2', '123', 'mail', TRUE, 'user2')," +
                        "('11111111-1111-1111-1111-111111111113','user3', '123', 'mail', TRUE, 'user3')";

        String createTopicTableQuery =
                "CREATE SEQUENCE topics_id_seq" +
                        "  INCREMENT 1" +
                        "  MINVALUE 1" +
                        "  MAXVALUE 9223372036854775807" +
                        "  START 1" +
                        "  CACHE 1;" +
                        "ALTER TABLE topics_id_seq" +
                        "  OWNER TO postgres;" +
                        "" +
                "CREATE TABLE topics" +
                        "(" +
                        "    uid UUID PRIMARY KEY NOT NULL," +
                        "    id INTEGER DEFAULT nextval('topics_id_seq'::REGCLASS) NOT NULL," +
                        "    title TEXT" +
                        ");" +
                        "CREATE UNIQUE INDEX topics_id_uindex ON topics (id);";

        String insertTopicsQuery =
                "INSERT INTO topics VALUES " +
                        "('22211111-1111-1111-1111-111111111111',1,'topic1')," +
                        "('33311111-1111-1111-1111-111111111111',2,'topic2')," +
                        "('44411111-1111-1111-1111-111111111111',3,'topic3')";


        String createTopicRowsQuery =
                "CREATE TABLE topic_rows" +
                        "(" +
                        "    topic_uid UUID NOT NULL," +
                        "    user_uid UUID NOT NULL," +
                        "    learn BOOLEAN," +
                        "    teach BOOLEAN," +
                        "    CONSTRAINT topic_rows_topics_uid_fk FOREIGN KEY (topic_uid) REFERENCES topics (uid)," +
                        "    CONSTRAINT topic_rows_users_uid_fk FOREIGN KEY (user_uid) REFERENCES users (uid)" +
                        ");" +
                        "CREATE UNIQUE INDEX topic_rows_topic_uid_user_uid_pk ON topic_rows (topic_uid, user_uid);";

        String insertTopicRowsQuery =
                "INSERT INTO topic_rows VALUES " +
                        "('22211111-1111-1111-1111-111111111111','11111111-1111-1111-1111-111111111111', TRUE, FALSE)," +
                        "('33311111-1111-1111-1111-111111111111','11111111-1111-1111-1111-111111111111', FALSE, FALSE)," +
                        "('44411111-1111-1111-1111-111111111111','11111111-1111-1111-1111-111111111111', FALSE, TRUE)";
        ;

        try (
                Connection conn = ds.getConnection();
                Statement st = conn.createStatement();
        ) {

            st.executeUpdate(createUsersTableQuery);
            st.executeUpdate(insertUsersQuery);

            st.executeUpdate(createTopicTableQuery);
            st.executeUpdate(insertTopicsQuery);

            st.executeUpdate(createTopicRowsQuery);
            st.executeUpdate(insertTopicRowsQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @After
    public void dropTables(){
        String dropAllTables =
                "DROP TABLE topic_rows; " +
                        "DROP TABLE topics; " +
                        "DROP TABLE users;" +
                        "DROP SEQUENCE topics_id_seq";

        try(
                Connection conn = ds.getConnection();
                Statement st = conn.createStatement();
        ){
            st.executeUpdate(dropAllTables);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    @Test
    public void setLearnPredicate() throws Exception {

        TopicRowsDAOPG topicRowsDAOPG = new TopicRowsDAOPG();
        topicRowsDAOPG.setDataSource(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        topicRowsDAOPG.setLearnPredicate(1L, user, false);

        assertEquals(
                "Нашел какие-то темы, после их удаления из обучения",
                topicRowsDAOPG.getLearnTopicsByUser(user).size(),
                0
        );

        topicRowsDAOPG.setLearnPredicate(30L, user, true);

        assertEquals(
                "Нашел какие-то темы после установки с неправильным id",
                topicRowsDAOPG.getLearnTopicsByUser(user).size(),
                0
        );

    }

    @Test
    public void setTeachPredicate() throws Exception {

        TopicRowsDAOPG topicRowsDAOPG = new TopicRowsDAOPG();
        topicRowsDAOPG.setDataSource(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        topicRowsDAOPG.setTeachPredicate(1L, user, true);

        assertEquals(
                topicRowsDAOPG.getTeachTopicsByUser(user).size(),
                2
        );

        topicRowsDAOPG.setLearnPredicate(30L, user, true);

        assertEquals(
                topicRowsDAOPG.getTeachTopicsByUser(user).size(),
                2
        );
    }

    @Test
    public void getAllByUser() throws Exception {

        TopicRowsDAOPG topicRowsDAOPG = new TopicRowsDAOPG();
        topicRowsDAOPG.setDataSource(ds);


        User user1 = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);

        User user2 = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111112"),
                "user2",
                "123",
                "user2",
                "mail",
                true);


        assertEquals(
                topicRowsDAOPG.getAllByUser(user1).size(),
                topicRowsDAOPG.getAllByUser(user2).size()
        );

        assertEquals(
                topicRowsDAOPG.getAllByUser(user1).size(),
                3
        );

    }

    @Test(expected = NullPointerException.class)
    public void getAllByUserNull() throws Exception {

        TopicRowsDAOPG topicRowsDAOPG = new TopicRowsDAOPG();
        topicRowsDAOPG.setDataSource(ds);

        topicRowsDAOPG.getAllByUser(null);

    }

    @Test
    public void getLearnTopicsByUser() throws Exception {
        TopicRowsDAOPG topicRowsDAOPG = new TopicRowsDAOPG();
        topicRowsDAOPG.setDataSource(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);


        assertTrue(
                "Не содержит первую строку в списке learn",
                topicRowsDAOPG.getLearnTopicsByUser(user).contains(
                        new Topic(
                                UUID.fromString("22211111-1111-1111-1111-111111111111"),
                                1,
                                "topic1"
                        )
                )
        );

        assertEquals(
                "Содержит больше одной строки в списке learn",
                topicRowsDAOPG.getLearnTopicsByUser(user).size(),
                1
        );

    }

    @Test
    public void getTeachTopicsByUser() throws Exception {

        TopicRowsDAOPG topicRowsDAOPG = new TopicRowsDAOPG();
        topicRowsDAOPG.setDataSource(ds);

        User user = new User(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1",
                "123",
                "user1",
                "mail",
                true);


        assertTrue(
                "Не содержит первую строку в списке learn",
                topicRowsDAOPG.getTeachTopicsByUser(user).contains(
                        new Topic(
                                UUID.fromString("44411111-1111-1111-1111-111111111111"),
                                3,
                                "topic3"
                        )
                )
        );

        assertEquals(
                "Содержит больше одной строки в списке learn",
                topicRowsDAOPG.getTeachTopicsByUser(user).size(),
                1
        );
    }

}