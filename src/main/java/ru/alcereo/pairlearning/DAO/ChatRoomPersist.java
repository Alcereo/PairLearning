package ru.alcereo.pairlearning.DAO;

import java.util.UUID;


public class ChatRoomPersist {

    private final UUID uid;
    private final User user;

    public ChatRoomPersist(UUID uid, User user) {
        this.uid = uid;
        this.user = user;
    }

    public ChatRoomPersist(){
        this(null, null);
    }

    public UUID getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

}
