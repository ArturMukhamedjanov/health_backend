package health.models.dto;

import lombok.Builder;
import health.models.Gender;

import javax.validation.constraints.Positive;


@Builder(toBuilder = true)
public record CustomerDto(
        Long id,
        String email,
        String password,
        Long userId,
        String firstName,
        String lastName,
        @Positive(message = "Age must be greater than 0")
        Integer age,
        @Positive(message = "Age must be greater than 0")
        Integer weight,
        Gender gender,
        @Positive(message = "Age must be greater than 0")
        Integer height
) {

}
