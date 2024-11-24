package health.models.dto;

import lombok.Builder;
import health.models.Gender;

@Builder
public record CustomerDTO(
    Long id,
    Long userId,
    String firstName,
    String lastName,
    Integer age,
    Integer weight,
    Gender gender,
    Integer height,
    String email
) {
    
}
