package health.services;

import health.models.Chat;
import health.models.Message;
import health.models.auth.Role;
import health.repos.MessageRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepo messageRepository;

    @InjectMocks
    private MessageService messageService;

    private Chat chat;
    private Message message;

    @BeforeEach
    void setUp() {
        chat = Chat.builder()
                .id(1L)
                .build();

        message = Message.builder()
                .id(1L)
                .chat(chat)
                .role(Role.CUSTOMER)
                .text("Hello, I have a question about my appointment.")
                .sendTime(Instant.now())
                .build();
    }

    @Test
    void getMessagesByChat_ShouldReturnMessagesForChat() {
        // Arrange
        List<Message> expectedMessages = List.of(message);
        when(messageRepository.getMessagesByChat(chat)).thenReturn(expectedMessages);

        // Act
        List<Message> result = messageService.getMessagesByChat(chat);

        // Assert
        assertEquals(expectedMessages, result);
        verify(messageRepository).getMessagesByChat(chat);
    }

    @Test
    void getMessageById_WhenMessageExists_ShouldReturnMessage() {
        // Arrange
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        // Act
        Optional<Message> result = messageService.getMessageById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(message, result.get());
        verify(messageRepository).findById(1L);
    }

    @Test
    void getMessageById_WhenMessageDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(messageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Message> result = messageService.getMessageById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(messageRepository).findById(999L);
    }

    @Test
    void saveOrUpdateMessage_ShouldSaveAndReturnMessage() {
        // Arrange
        Message newMessage = Message.builder()
                .chat(chat)
                .role(Role.DOCTOR)
                .text("I can help you with that. What's your question?")
                .sendTime(Instant.now())
                .build();
                
        when(messageRepository.save(newMessage)).thenReturn(
                Message.builder()
                        .id(2L)
                        .chat(chat)
                        .role(Role.DOCTOR)
                        .text("I can help you with that. What's your question?")
                        .sendTime(newMessage.getSendTime())
                        .build());

        // Act
        Message result = messageService.saveOrUpdateMessage(newMessage);

        // Assert
        assertEquals(2L, result.getId());
        assertEquals(chat, result.getChat());
        assertEquals(Role.DOCTOR, result.getRole());
        assertEquals("I can help you with that. What's your question?", result.getText());
        assertNotNull(result.getSendTime());
        verify(messageRepository).save(newMessage);
    }

    @Test
    void deleteMessage_ShouldDeleteMessage() {
        // Act
        messageService.deleteMessage(message);

        // Assert
        verify(messageRepository).delete(message);
    }
}