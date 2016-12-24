package ru.alcereo.pairlearning.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.DAO.ChatRoomPersist;
import ru.alcereo.pairlearning.DAO.ChatRoomDAO;
import ru.alcereo.pairlearning.DAO.ChatRoomDAOMock;
import ru.alcereo.pairlearning.DAO.User;

import java.util.List;
import java.util.UUID;

/**
 * Created by alcereo on 24.12.16.
 */
public class ChatRoomProvider {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomProvider.class);

    private static final ChatRoomDAO chatRoomDAO = new ChatRoomDAOMock();

    public static void addUserToChatRoom(User user) {

        List<ChatRoomPersist> roomList = chatRoomDAO.getEmptyRooms();

        //Пока выбираем первого свободного

        ChatRoomPersist newChatRoomPersist;

        if (roomList.size()>0){
            newChatRoomPersist = new ChatRoomPersist(roomList.get(0).getUid(), user);

            log.debug("Присоединили пользователя: {} в комнату: {}",user, newChatRoomPersist.getUid());
        }else {
            newChatRoomPersist = new ChatRoomPersist(UUID.randomUUID(), user);

            log.debug("Создали для пользователя: {} комнату: {}",user, newChatRoomPersist.getUid());
        }

        chatRoomDAO.insertChatRoom(newChatRoomPersist);

    }

}
