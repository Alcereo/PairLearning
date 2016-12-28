package ru.alcereo.pairlearning.Service.Chat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.TopicService;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.util.List;

public class TopicInviteChecker implements InviteChecker{

    private static final Logger log = LoggerFactory.getLogger(TopicInviteChecker.class);

    @Override
    public boolean usersInvitable(List<UserFront> users) throws TopicServiceException {

        try {
            return TopicService.usersHaveIntersectionTopics(users);
        } catch (TopicServiceException e) {
            log.warn(e.getLocalizedMessage());
            throw e;
        }
    }
}
