package health.models.dto;

import health.models.auth.Role;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Builder(toBuilder = true)
public record MessageDto(
        Long id,
        Long chatId,
        @NotBlank(message = "Message text is required")
        String text,
        Role role,
        Instant sendTime
) {
}
