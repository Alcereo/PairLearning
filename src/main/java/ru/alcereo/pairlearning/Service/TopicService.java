package ru.alcereo.pairlearning.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TopicService {

    private static final Logger log = LoggerFactory.getLogger(TopicService.class);

    private static final UsersDAO users = new UsersDAOPG();
    private static TopicRowsDAO topicRows = new TopicRowsDAOPG();

    public static List<TopicRowFront> getUserTopic(UserFront user) {

        List<TopicRowFront> result = new ArrayList<>();

        if (user!=null){
            User userModel = users.findByLogin(user.getLogin());

            if (userModel!=null)
                for (TopicRow topicRow : topicRows.getAllByUser(userModel))
                    result.add(topicRow);

        }


        return result;

    }

    public static boolean setTopicRow(UserFront user, Long id, TopicPredicateSide side, boolean predicate) {
        boolean result = false;

        if (user != null & side != null) {

            User userModel = users.findByLogin(user.getLogin());

            switch (side) {
                case LEARN:
                    log.debug("Меняем LEARN на {} у id:{}",predicate, id);
                    result = topicRows.setLearnPredicate(id, userModel, predicate);
                    break;
                case TEACH:
                    log.debug("Меняем TEACH на {} у id:{}",predicate, id);
                    result = topicRows.setTeachPredicate(id, userModel, predicate);
                    break;
            }

        }

        return result;
    }

    public static boolean usersHaveIntersectionTopics(List<UserFront> usersList) {

        boolean result=false;

        log.debug("Определяем возможность входа для: {}",usersList);

        if (usersList.size() < 2)
            result = true;

        log.debug("Прошли по количеству: {}",result);

        if (usersList.size()==2){

            User user1 = users.findByLogin(usersList.get(0).getLogin());
            User user2 = users.findByLogin(usersList.get(1).getLogin());

            Set<Topic> userSet1 = topicRows.getLearnTopicsByUser(user1);
            Set<Topic> userSet2 = topicRows.getTeachTopicsByUser(user2);

            userSet1.retainAll(userSet2);

            log.debug("Количество пересечений 1: {}",userSet1.size());

            if (userSet1.size()>0){

                userSet1 = topicRows.getTeachTopicsByUser(user1);
                userSet2 = topicRows.getLearnTopicsByUser(user2);

                userSet1.retainAll(userSet2);

                log.debug("Количество пересечений 2: {}",userSet1);

                if (userSet1.size()>0)
                    result = true;

            }

        }

        return result;
    }

}
