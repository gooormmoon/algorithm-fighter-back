package gooroommoon.algofi_core.chat.repository;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ChatRoomRepository extends JpaRepository<Chatroom, Long> {
    boolean existsByChatroomId(Long chatroomId);

    Optional<Chatroom> findByChatroomName(String name);
}
