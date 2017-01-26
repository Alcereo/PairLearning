package ru.alcereo.pairlearning.DAO.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "topics")
public class TopicEntity implements Serializable {

    @Id
    private UUID uid;

    @Column
    private long id;

    @Column
    private String title;

    @OneToMany(
            targetEntity = TopicRowEntity.class,
            mappedBy = "topic",
            fetch = FetchType.LAZY
    )
    private List<TopicRowEntity> topicRows;

    public List<TopicRowEntity> getTopicRows() {
        return topicRows;
    }

    public void setTopicRows(List<TopicRowEntity> topicRows) {
        this.topicRows = topicRows;
    }

    public TopicEntity() {
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "TopicEntity{" +
                "uid=" + uid +
                ", id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

}
