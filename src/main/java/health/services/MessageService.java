package health.services;

import health.models.Chat;
import health.models.Message;
import health.repos.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepo messageRepository;

    public List<Message> getMessagesByChat(Chat chat) {
        return messageRepository.getMessagesByChat(chat);
    }

    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    public Message saveOrUpdateMessage(Message message) {
        return messageRepository.save(message);
    }

    public void deleteMessage(Message message) {
        messageRepository.delete(message);
    }
}
