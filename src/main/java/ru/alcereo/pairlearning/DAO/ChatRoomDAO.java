package ru.alcereo.pairlearning.DAO;


import java.util.List;
import java.util.UUID;

public interface ChatRoomDAO {

    ChatRoomPersist getChatRoomByUser(User user);
    List<ChatRoomPersist> getChatRoomsByUID(UUID uuid);

    User getChatRoomPartner(User user);

    List<ChatRoomPersist> getEmptyRooms();

    void insertChatRoom(ChatRoomPersist chatRoomPersist);
    void deleteChatRoom(ChatRoomPersist chatRoomPersist);

    void removeChatRoom(User chatPartner);
}
