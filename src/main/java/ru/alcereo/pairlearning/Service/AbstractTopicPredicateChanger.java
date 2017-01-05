package ru.alcereo.pairlearning.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;


abstract class AbstractTopicPredicateChanger implements TopicRowChanger{

    private static final Logger log = LoggerFactory.getLogger(AbstractTopicPredicateChanger.class);

    protected boolean value;
    protected long id = -1;

    @Override
    public TopicRowChanger setPredicateValue(boolean value) {
        this.value = value;
        return this;
    }

    @Override
    public TopicRowChanger setTopicId(Long id) {
        this.id = id;
        return this;
    }

}
