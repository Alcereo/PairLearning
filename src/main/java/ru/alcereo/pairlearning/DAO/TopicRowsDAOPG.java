package ru.alcereo.pairlearning.DAO;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.models.Topic;
import ru.alcereo.pairlearning.DAO.models.User;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class TopicRowsDAOPG implements TopicRowsDAO {

    private static final Logger log = LoggerFactory.getLogger(TopicRowsDAOPG.class);

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


    @Override
    public boolean setLearnPredicate(Long id, User user, boolean predicate) {

        boolean result=false;

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "WITH upsert AS" +
                                "(UPDATE topic_rows" +
                                "  SET learn = ?" +
                                "  WHERE" +
                                "    topic_uid = (SELECT uid from topics WHERE id = ?) AND" +
                                "      user_uid = ?" +
                                "  RETURNING *)" +
                                "INSERT INTO topic_rows" +
                                "  SELECT" +
                                "    (SELECT uid from topics WHERE id = ?)," +
                                "    ?," +
                                "    ?," +
                                "    FALSE" +
                                "  WHERE NOT EXISTS" +
                                "  (SELECT *" +
                                "   FROM upsert)");
        ){

            st.setBoolean(1,predicate);
            st.setLong(2,id);
            st.setObject(3, user.getUid());

            st.setLong(4,id);
            st.setObject(5, user.getUid());
            st.setBoolean(6,predicate);

            st.executeUpdate();

            log.debug("Выполнили запрос learn: id:{}, pred:{}, usermod:{}",id,predicate, user.getUid());

            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
        }

        return result;
    }

    @Override
    public boolean setTeachPredicate(Long id, User userModel, boolean predicate) {
        boolean result=false;

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "WITH upsert AS" +
                                "(UPDATE topic_rows" +
                                "  SET teach = ?" +
                                "  WHERE" +
                                "    topic_uid = (SELECT uid from topics WHERE id = ?) AND" +
                                "      user_uid = ?" +
                                "  RETURNING *)" +
                                "INSERT INTO topic_rows" +
                                "  SELECT" +
                                "    (SELECT uid from topics WHERE id = ?)," +
                                "    ?," +
                                "    FALSE," +
                                "    ?" +
                                "  WHERE NOT EXISTS" +
                                "  (SELECT *" +
                                "   FROM upsert)");
        ){

            st.setBoolean(1,predicate);
            st.setLong(2,id);
            st.setObject(3, userModel.getUid());

            st.setLong(4,id);
            st.setObject(5, userModel.getUid());
            st.setBoolean(6,predicate);

            st.executeUpdate();

            log.debug("Выполнили запрос teach: id:{}, pred:{}, usermod:{}",id,predicate, userModel.getUid());

            result = true;

        } catch (SQLException e) {
//            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
        }

        return result;
    }

    @Override
    public List<TopicRow> getAllByUser(User userModel) {
        List<TopicRow> result=new ArrayList<>();

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT\n" +
                                "  uid,\n" +
                                "  id,\n" +
                                "  title,\n" +
                                "  CASE WHEN learn IS NULL THEN FALSE ELSE learn END AS learn,\n" +
                                "  CASE WHEN teach IS NULL THEN FALSE ELSE teach END AS teach\n" +
                                "FROM\n" +
                                "  topics LEFT JOIN topic_rows ON topics.uid = topic_rows.topic_uid AND topic_rows.user_uid = ?\n");
        ){

            st.setObject(1,userModel.getUid());

            try (ResultSet resultSet = st.executeQuery()){
                while (resultSet.next())
                    result.add(new TopicRow(
                            resultSet.getBoolean("learn"),
                            resultSet.getBoolean("teach"),
                            userModel,
                            new Topic(
                                    UUID.fromString(resultSet.getString("uid")),
                                    resultSet.getLong("id"),
                                    resultSet.getString("title")
                            )

                    ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
        }

        return result;
    }


    @Override
    public Set<Topic> getLearnTopicsByUser(User user) {
        Set<Topic> result=new HashSet<>();

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT\n" +
                                "  topics.uid,\n" +
                                "  topics.id,\n" +
                                "  topics.title\n" +
                                "FROM\n" +
                                "  topic_rows,\n" +
                                "  topics\n" +
                                "WHERE\n" +
                                "  topic_rows.user_uid = ? AND\n" +
                                "    topic_rows.topic_uid = topics.uid AND\n" +
                                "    topic_rows.learn");
        ){

            st.setObject(1, user.getUid());

            try(ResultSet resultSet = st.executeQuery()){
                while (resultSet.next())
                    result.add(
                            new Topic(
                                    UUID.fromString(resultSet.getString("uid")),
                                    resultSet.getLong("id"),
                                    resultSet.getString("title")
                            )
                    );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
        }

        return result;
    }


    @Override
    public Set<Topic> getTeachTopicsByUser(User user) {
        Set<Topic> result=new HashSet<>();

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT\n" +
                                "  topics.uid,\n" +
                                "  topics.id,\n" +
                                "  topics.title\n" +
                                "FROM\n" +
                                "  topic_rows,\n" +
                                "  topics\n" +
                                "WHERE\n" +
                                "  topic_rows.user_uid = ? AND\n" +
                                "    topic_rows.topic_uid = topics.uid AND\n" +
                                "    topic_rows.teach");
        ){

            st.setObject(1, user.getUid());

            try(ResultSet resultSet = st.executeQuery()){
                while (resultSet.next())
                    result.add(
                            new Topic(
                                    UUID.fromString(resultSet.getString("uid")),
                                    resultSet.getLong("id"),
                                    resultSet.getString("title")
                            )
                    );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
        }

        return result;
    }

    public boolean addTopic(Topic topic){

        boolean result=false;

        try(
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO topics VALUES (?,?,?)");
        ){

            st.setObject(1, topic.getUid());
            st.setLong(2,topic.getId());
            st.setString(3, topic.getTitle());

            result = st.executeUpdate()==1;

        } catch (SQLException e) {
            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
        }

        return result;

    }


    public static void main(String[] args) {

        TopicRowsDAOPG topicRowsDAOPG = new TopicRowsDAOPG();

        System.out.println();

    }

}
