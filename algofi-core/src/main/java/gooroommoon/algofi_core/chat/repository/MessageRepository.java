package gooroommoon.algofi_core.chat.repository;

import gooroommoon.algofi_core.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatroomIdChatroomId(String chatroomId);
}
