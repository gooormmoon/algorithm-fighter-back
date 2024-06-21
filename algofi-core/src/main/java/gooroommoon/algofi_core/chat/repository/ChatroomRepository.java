package gooroommoon.algofi_core.chat.repository;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ChatroomRepository extends JpaRepository<Chatroom, String> {
    boolean existsByChatroomId(String chatroomId);

    Optional<Chatroom> findByChatroomName(String name);

    Optional<Chatroom> findByChatroomId(String chatroomId);
}
