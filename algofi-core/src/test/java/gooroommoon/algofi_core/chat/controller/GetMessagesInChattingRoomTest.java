package gooroommoon.algofi_core.chat.controller;

import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class GetMessagesInChattingRoomTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    @DisplayName("채팅방의 메시지 가져오기")
    public void testGetMessagesInChattingRoom() throws Exception {
        UUID chatRoomId = UUID.randomUUID(); // 랜덤 UUID 생성

        List<MessageDTO> messages = Arrays.asList(
                MessageDTO.builder()
                        .type(MessageType.TALK)
                        .messageId(1L)
                        .chatRoomId(chatRoomId)
                        .senderId(1L) // Mock senderId
                        .content("Hello!")
                        .createdDate(LocalDateTime.now().minusHours(1)) // Mock createdDate
                        .build(),
                MessageDTO.builder()
                        .type(MessageType.TALK)
                        .messageId(2L)
                        .chatRoomId(chatRoomId)
                        .senderId(1L) // Mock senderId
                        .content("Hi!")
                        .createdDate(LocalDateTime.now())
                        .build()
        );

        given(chatService.getMessagesInChattingRoom(chatRoomId)).willReturn(messages);


        mockMvc.perform(get("/chat/{chatRoomId}/messages", chatRoomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content").value("Hello!"))
                .andExpect(jsonPath("$[1].content").value("Hi!"))
                .andDo(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                });
    }
}
