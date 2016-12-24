package ru.alcereo.pairlearning.DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ChatRoomDAOMock implements ChatRoomDAO {

    private static final List<ChatRoomPersist> CHAT_ROOM_PERSIST_LIST = new ArrayList<>();

    @Override
    public ChatRoomPersist getChatRoomByUser(User user) {
        ChatRoomPersist result = null;

        for (ChatRoomPersist chatRoomPersist : CHAT_ROOM_PERSIST_LIST)
            if (chatRoomPersist.getUser().equals(user))
                result = chatRoomPersist;

        return result;
    }

    @Override
    public List<ChatRoomPersist> getChatRoomsByUID(UUID uuid) {
        List<ChatRoomPersist> resultList = new ArrayList<>();

        for (ChatRoomPersist chatRoomPersist : CHAT_ROOM_PERSIST_LIST)
            if (chatRoomPersist.getUid().equals(uuid))
                resultList.add(chatRoomPersist);

        return resultList;
    }

    @Override
    public User getChatRoomPartner(User user) {
        User result = null;

        ChatRoomPersist userChatRoomPersist = getChatRoomByUser(user);

        if (userChatRoomPersist !=null)
            for(ChatRoomPersist chatRoomPersist : getChatRoomsByUID(userChatRoomPersist.getUid()))
                if (!chatRoomPersist.getUser().equals(user))
                    result = chatRoomPersist.getUser();

        return result;
    }

    @Override
    public List<ChatRoomPersist> getEmptyRooms() {
        List<ChatRoomPersist> result = new ArrayList<>();

        for(ChatRoomPersist chatRoomPersist : CHAT_ROOM_PERSIST_LIST)
            if (getChatRoomsByUID(chatRoomPersist.getUid()).size()==1)
                result.add(chatRoomPersist);

        return result;
    }

    @Override
    public void insertChatRoom(ChatRoomPersist chatRoomPersist) {
        CHAT_ROOM_PERSIST_LIST.add(chatRoomPersist);
    }

    @Override
    public void deleteChatRoom(ChatRoomPersist chatRoomPersist) {
        CHAT_ROOM_PERSIST_LIST.remove(chatRoomPersist);
    }

    @Override
    public void removeChatRoom(User user) {

        ChatRoomPersist userChatRoomPersist = getChatRoomByUser(user);

        if (userChatRoomPersist !=null)
            for (ChatRoomPersist chatRoomPersist :getChatRoomsByUID(userChatRoomPersist.getUid()))
                CHAT_ROOM_PERSIST_LIST.remove(chatRoomPersist);

    }
}
