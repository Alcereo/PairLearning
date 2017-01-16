package ru.alcereo.pairlearning.DAO.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
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
