package gooroommoon.algofi_core.chat.repository;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ChatRoomRepository extends JpaRepository<Chatroom, String> {
    boolean existsByChatroomId(String  chatroomId);

    Optional<Chatroom> findByChatroomName(String name);

    @Query("select c from Chatroom c where c.chatroomId = :chatroomId")
    Optional<Chatroom> findByChatroomId(@Param("chatroomId")String chatroomId);
}
