package ru.alcereo.pairlearning.Service;

import ma.glasnost.orika.impl.ConfigurableMapper;
import ru.alcereo.pairlearning.Service.models.TopicRow;
import ru.alcereo.pairlearning.Service.models.User;


public class EntityMapper extends ConfigurableMapper {

    public <FROM_TYPE> User wrapToUser(FROM_TYPE notUser){
        return map(notUser, User.class);
    }

    public <FROM_TYPE> TopicRow wrapToTopicRow(FROM_TYPE notTopicRow) {
        return new EntityMapper().map(notTopicRow, TopicRow.class);
    }

}
