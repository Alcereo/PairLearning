package ru.alcereo.pairlearning.Service.TopicService;

import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.TopicRowFront;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.util.List;


public interface TopicService {
    Option<List<TopicRowFront>,TopicServiceException> getUserTopicOpt(UserFront user_n);

    Option<Boolean, TopicServiceException> usersHaveIntersectionTopics(UserFront user1_n, UserFront user2_n);

    Option<Boolean, TopicServiceException> setTopicRow(TopicChangeData data);
}
