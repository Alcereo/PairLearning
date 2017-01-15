package ru.alcereo.pairlearning.Service.Chat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.Service.Chat.exceptions.ChatInviteException;
import ru.alcereo.pairlearning.Service.TopicService.TopicService;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.util.List;

public class TopicInviteChecker implements InviteChecker{

    private static final Logger log = LoggerFactory.getLogger(TopicInviteChecker.class);

    private TopicService topicService;

    public void setTopicService(TopicService topicService) {
        this.topicService = topicService;
    }

    @Override
    public Option<Boolean, ChatInviteException> usersInvitable(List<UserFront> users_n){

        return Option.asNotNullWithExceptionOption(users_n)
        .flatMap(users -> {
            if (users.size()<2)
                return Option.asOption(true);
            else if (users.size()==2)
                return topicService.usersHaveIntersectionTopics(users.get(0), users.get(1));
            else
                return Option.asOption(false);
        })
        .wrapException(TopicInviteChecker::ChatInviteExceptionWrapper);
    }

    private static ChatInviteException ChatInviteExceptionWrapper(Throwable cause) {
        log.warn(cause.getMessage());

        return new ChatInviteException(
                "Ошибка сервиса тем. "+cause.getLocalizedMessage(),
                cause);
    }
}
