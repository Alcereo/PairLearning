package ru.alcereo.pairlearning.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.DAO.Entities.TopicRowEntity;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.Topic;
import ru.alcereo.pairlearning.Service.models.TopicRow;
import ru.alcereo.pairlearning.Service.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class TopicRowsDAOPG implements TopicRowsDAO {

    private static final Logger log = LoggerFactory.getLogger(TopicRowsDAOPG.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }


    @Override
    public void setLearnPredicate(Long id, User user, boolean predicate) throws TopicRowDataError {

        Objects.requireNonNull(user, "User == null");
        Objects.requireNonNull(id, "User == null");

        try(
                Connection conn = dataSource.getConnection();
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
                                "    FALSE " +
                                "FROM topics  " +
                                "  WHERE id = ? AND NOT EXISTS" +
                                "  (SELECT *" +
                                "   FROM upsert)");
        ){

            st.setBoolean(1,predicate);
            st.setLong(2,id);
            st.setObject(3, user.getUid());

            st.setLong(4,id);
            st.setObject(5, user.getUid());
            st.setBoolean(6,predicate);

            st.setLong(7,id);

            st.executeUpdate();

            log.debug("Выполнили запрос learn: id:{}, pred:{}, usermod:{}",id,predicate, user.getUid());

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new TopicRowDataError("Ошибка обращения к данным по темам", e);
        }

    }

    @Override
    public void setTeachPredicate(Long id, User userModel, boolean predicate) throws TopicRowDataError {


        try(
                Connection conn = dataSource.getConnection();
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


        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new TopicRowDataError("Ошибка обращения к данным по темам", e);
        }

    }

    @Override
    public List<TopicRow> getAllByUser(User user) throws TopicRowDataError {
        List<TopicRow> result=new ArrayList<>();

        Objects.requireNonNull(user, "user == null");

        try(
                Connection conn = dataSource.getConnection();
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

            st.setObject(1,user.getUid());

            try (ResultSet resultSet = st.executeQuery()){
                while (resultSet.next())
                    result.add(new TopicRow(
                            resultSet.getBoolean("learn"),
                            resultSet.getBoolean("teach"),
                            user,
                            new Topic(
                                    UUID.fromString(resultSet.getString("uid")),
                                    resultSet.getLong("id"),
                                    resultSet.getString("title")
                            )

                    ));
            }

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new TopicRowDataError("Ошибка обращения к данным по темам", e);
        }

        return result;
    }

    @Override
    public Option<List<TopicRowEntity>, TopicRowDataError> getAllByUserUID(UUID uuid){
        return Option.asException(new TopicRowDataError("NOT IMPLEMENTED!!"));
    }

    @Override
    public Set<Topic> getLearnTopicsByUser(User user) throws TopicRowDataError {
        return getLearnTopicsByUserUID(user.getUid());
    }

    @Override
    public Set<Topic> getLearnTopicsByUserUID(UUID uuid) throws TopicRowDataError {
        Set<Topic> result=new HashSet<>();

        UUID userUid = uuid;

        try(
                Connection conn = dataSource.getConnection();
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

            st.setObject(1, userUid);

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
            log.warn(e.getLocalizedMessage());
            throw new TopicRowDataError("Ошибка обращения к данным по темам", e);
        }

        return result;
    }

    @Override
    public Set<Topic> getTeachTopicsByUser(User user) throws TopicRowDataError {
        return getTeachTopicsByUserUID(user.getUid());
    }

    @Override
    public Set<Topic> getTeachTopicsByUserUID(UUID uuid) throws TopicRowDataError {
        Set<Topic> result=new HashSet<>();

        try(
                Connection conn = dataSource.getConnection();
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

            st.setObject(1, uuid);

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
            log.warn(e.getLocalizedMessage());
            throw new TopicRowDataError("Ошибка обращения к данным по темам", e);
        }

        return result;
    }


    public boolean addTopic(Topic topic) throws TopicRowDataError {

        boolean result=false;

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO topics VALUES (?,?,?)");
        ){

            st.setObject(1, topic.getUid());
            st.setLong(2,topic.getId());
            st.setString(3, topic.getTitle());

            result = st.executeUpdate()==1;

        } catch (SQLException e) {
            log.warn(e.getLocalizedMessage());
            throw new TopicRowDataError("Ошибка обращения к данным по темам", e);
        }

        return result;

    }


}
