package health.models.mapper;

import health.models.Message;
import health.models.dto.MessageDto;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public Message mapFromDto(MessageDto dto) {
        var builder = Message.builder();
        if(dto.text() != null){
            builder.text(dto.text());
        }
        return Message.builder()
                .text(dto.text())
                .build();
    }

    public MessageDto mapToDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .text(message.getText())
                .role(message.getRole())
                .sendTime(message.getSendTime())
                .build();
    }
}
