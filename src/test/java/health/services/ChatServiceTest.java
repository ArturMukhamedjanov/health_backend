package health.services;

import health.models.*;
import health.repos.ChatRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepo chatRepository;

    @InjectMocks
    private ChatService chatService;

    private Doctor doctor;
    private Customer customer;
    private Clinic clinic;
    private Chat chat;

    @BeforeEach
    void setUp() {
        clinic = Clinic.builder()
                .id(1L)
                .name("Test Clinic")
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .speciality("Cardiology")
                .clinic(clinic)
                .build();

        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .build();

        chat = Chat.builder()
                .id(1L)
                .doctor(doctor)
                .customer(customer)
                .clinic(clinic)
                .build();
    }

    @Test
    void getChatByDoctorAndCustomer_WhenChatExists_ShouldReturnChat() {
        // Arrange
        when(chatRepository.findChatByDoctorAndCustomer(doctor, customer))
                .thenReturn(Optional.of(chat));

        // Act
        Optional<Chat> result = chatService.getChatByDoctorAndCustomer(doctor, customer);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(chat, result.get());
        verify(chatRepository).findChatByDoctorAndCustomer(doctor, customer);
    }

    @Test
    void getChatByDoctorAndCustomer_WhenChatDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(chatRepository.findChatByDoctorAndCustomer(doctor, customer))
                .thenReturn(Optional.empty());

        // Act
        Optional<Chat> result = chatService.getChatByDoctorAndCustomer(doctor, customer);

        // Assert
        assertTrue(result.isEmpty());
        verify(chatRepository).findChatByDoctorAndCustomer(doctor, customer);
    }

    @Test
    void getChatsByDoctor_ShouldReturnChatsForDoctor() {
        // Arrange
        List<Chat> expectedChats = List.of(chat);
        when(chatRepository.getChatsByDoctor(doctor)).thenReturn(expectedChats);

        // Act
        List<Chat> result = chatService.getChatsByDoctor(doctor);

        // Assert
        assertEquals(expectedChats, result);
        verify(chatRepository).getChatsByDoctor(doctor);
    }

    @Test
    void getChatsByCustomer_ShouldReturnChatsForCustomer() {
        // Arrange
        List<Chat> expectedChats = List.of(chat);
        when(chatRepository.getChatsByCustomer(customer)).thenReturn(expectedChats);

        // Act
        List<Chat> result = chatService.getChatsByCustomer(customer);

        // Assert
        assertEquals(expectedChats, result);
        verify(chatRepository).getChatsByCustomer(customer);
    }

    @Test
    void getChatsByClinic_ShouldReturnChatsForClinic() {
        // Arrange
        List<Chat> expectedChats = List.of(chat);
        when(chatRepository.getChatByClinic(clinic)).thenReturn(expectedChats);

        // Act
        List<Chat> result = chatService.getChatsByClinic(clinic);

        // Assert
        assertEquals(expectedChats, result);
        verify(chatRepository).getChatByClinic(clinic);
    }

    @Test
    void getChatById_WhenChatExists_ShouldReturnChat() {
        // Arrange
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        // Act
        Optional<Chat> result = chatService.getChatById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(chat, result.get());
        verify(chatRepository).findById(1L);
    }

    @Test
    void getChatById_WhenChatDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(chatRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Chat> result = chatService.getChatById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(chatRepository).findById(999L);
    }

    @Test
    void saveOrUpdateChat_ShouldSaveAndReturnChat() {
        // Arrange
        Chat newChat = Chat.builder()
                .doctor(doctor)
                .customer(customer)
                .clinic(clinic)
                .build();
                
        when(chatRepository.save(newChat)).thenReturn(
                Chat.builder()
                        .id(2L)
                        .doctor(doctor)
                        .customer(customer)
                        .clinic(clinic)
                        .build());

        // Act
        Chat result = chatService.saveOrUpdateChat(newChat);

        // Assert
        assertEquals(2L, result.getId());
        assertEquals(doctor, result.getDoctor());
        assertEquals(customer, result.getCustomer());
        assertEquals(clinic, result.getClinic());
        verify(chatRepository).save(newChat);
    }

    @Test
    void deleteChat_ShouldDeleteChat() {
        // Act
        chatService.deleteChat(chat);

        // Assert
        verify(chatRepository).delete(chat);
    }
}