package health.services;

import health.models.*;
import health.repos.ChatRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepo chatRepository;

    public Optional<Chat> getChatByDoctorAndCustomer(Doctor doctor, Customer customer) {
        return chatRepository.findChatByDoctorAndCustomer(doctor, customer);
    }

    public List<Chat> getChatsByDoctor(Doctor doctor) {
        return chatRepository.getChatsByDoctor(doctor);
    }

    public List<Chat> getChatsByCustomer(Customer customer) {
        return chatRepository.getChatsByCustomer(customer);
    }

    public List<Chat> getChatsByClinic(Clinic clinic) {
        return chatRepository.getChatByClinic(clinic);
    }

    public Optional<Chat> getChatById(Long id) {
        return chatRepository.findById(id);
    }

    public Chat saveOrUpdateChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public void deleteChat(Chat chat) {
        chatRepository.delete(chat);
    }
}
