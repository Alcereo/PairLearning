package ru.alcereo.pairlearning.Service.TopicService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


abstract class AbstractTopicPredicateChanger implements TopicRowChanger{

    private static final Logger log = LoggerFactory.getLogger(AbstractTopicPredicateChanger.class);

    protected boolean value;
    protected Long id = null;

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
