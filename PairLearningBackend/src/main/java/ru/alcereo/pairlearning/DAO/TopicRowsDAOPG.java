package ru.alcereo.pairlearning.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.Entities.TopicEntity;
import ru.alcereo.pairlearning.DAO.Entities.TopicRowEntity;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.Topic;
import ru.alcereo.pairlearning.Service.models.UserFront;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Lazy
@Repository
public class TopicRowsDAOPG implements TopicRowsDAO {

    private static final Logger log = LoggerFactory.getLogger(TopicRowsDAOPG.class);

    private DataSource dataSource;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }


    @Override
    public Option<Boolean, TopicRowDataError> setLearnPredicate(Long id, UserFront user, boolean predicate) {

        Option<Boolean,TopicRowDataError> result = Option.asOption(true);

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
            result = Option.asException(new TopicRowDataError("Ошибка обращения к данным по темам", e));
        }

        return result;

    }

    @Override
    public Option<Boolean, TopicRowDataError> setTeachPredicate(Long id, UserFront userModel, boolean predicate){

        Option<Boolean,TopicRowDataError> result = Option.asOption(true);

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
            result = Option.asException(new TopicRowDataError("Ошибка обращения к данным по темам", e));
        }

        return result;

    }

    @Override
    public Option<List<TopicRowEntity>, TopicRowDataError> getAllByUserUIDOpt(UUID uuid){
        return Option.asException(new TopicRowDataError("NOT IMPLEMENTED!!"));
    }

    @Override
    public Option<Set<TopicEntity>,TopicRowDataError> getLearnTopicsByUserUID(UUID uuid) {
        return Option.asException(new TopicRowDataError("NOT IMPLEMENTED!"));
    }

    @Override
    public Option<Set<TopicEntity>,TopicRowDataError>  getTeachTopicsByUserUID(UUID uuid) {
        return Option.asException(new TopicRowDataError("NOT IMPLEMENTED!"));
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
