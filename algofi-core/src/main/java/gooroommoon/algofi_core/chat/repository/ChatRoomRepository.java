package gooroommoon.algofi_core.chat.repository;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface ChatRoomRepository extends JpaRepository<Chatroom, Long> {
    boolean existsByChatroomId(UUID chatroomId);

    Optional<Chatroom> findByChatroomName(String name);

    Optional<Chatroom> findByChatroomId(UUID chatRoomId);

    @Query(value = "SELECT * FROM Chatroom", nativeQuery = true)
    List<Chatroom> findAllChatroom();
}
