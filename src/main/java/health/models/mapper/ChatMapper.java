package health.models.mapper;

import health.models.Chat;
import health.models.dto.ChatDto;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public Chat mapFromDto(ChatDto dto) {
        return Chat.builder()
                .id(dto.id())
                .build();
    }

    public ChatDto mapToDto(Chat chat) {
        return ChatDto.builder()
                .id(chat.getId())
                .customerId(chat.getCustomer().getId())
                .doctorId(chat.getDoctor().getId())
                .clinicId(chat.getClinic().getId())
                .doctorName(chat.getDoctor().getFirstName() + " " + chat.getDoctor().getLastName())
                .customerName(chat.getCustomer().getFirstName() + " " + chat.getCustomer().getLastName())
                .build();
    }

}
