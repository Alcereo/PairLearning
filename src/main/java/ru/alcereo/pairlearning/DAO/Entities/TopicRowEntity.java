package ru.alcereo.pairlearning.DAO.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "topic_rows")
public class TopicRowEntity implements Serializable{

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private TopicEntity topic;

    @Column
    private boolean learn;

    @Column
    private boolean teach;

    public TopicRowEntity() {
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public TopicEntity getTopic() {
        return topic;
    }

    public void setTopic(TopicEntity topic) {
        this.topic = topic;
    }

    public boolean isLearn() {
        return learn;
    }

    public void setLearn(boolean learn) {
        this.learn = learn;
    }

    public boolean isTeach() {
        return teach;
    }

    public void setTeach(boolean teach) {
        this.teach = teach;
    }

    @Override
    public String toString() {
        return "TopicRowEntity{" +
                "user=" + user +
                ", topic=" + topic +
                ", learn=" + learn +
                ", teach=" + teach +
                '}';
    }

}
