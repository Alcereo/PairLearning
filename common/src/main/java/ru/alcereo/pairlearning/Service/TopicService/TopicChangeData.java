package ru.alcereo.pairlearning.Service.TopicService;

import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.io.Serializable;
import java.util.Objects;

public class TopicChangeData implements Serializable{

    private static final long serialVersionUID = 1L;

    private boolean value;
    private Long id = null;
    private TopicPredicate predicate;
    private UserFront user;

    public TopicChangeData(boolean value, Long id, TopicPredicate predicate, UserFront user) {
        this.value      = Objects.requireNonNull(value);
        this.id         = Objects.requireNonNull(id);
        this.predicate  = Objects.requireNonNull(predicate);
        this.user       = Objects.requireNonNull(user);
    }

    public boolean getValue() {
        return value;
    }

    public Long getId() {
        return id;
    }

    public TopicPredicate getPredicate() {
        return predicate;
    }

    public UserFront getUser() {
        return user;
    }
}
