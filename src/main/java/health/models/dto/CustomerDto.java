package health.models.dto;

import lombok.Builder;
import health.models.Gender;

import java.util.Optional;

@Builder(toBuilder = true)
public record CustomerDto(
        Optional<Long> id,
        Optional<String> email,
        Optional<String> password,
        Optional<Long> userId,
        Optional<String> firstName,
        Optional<String> lastName,
        Optional<Integer> age,
        Optional<Integer> weight,
        Optional<Gender> gender,
        Optional<Integer> height
) {
    public static class CustomerDtoBuilder{
        private Optional<Long> id = Optional.empty();
        private Optional<String> email = Optional.empty();
        private Optional<String> password = Optional.empty();
        private Optional<Long> userId = Optional.empty();
        private Optional<String> firstName = Optional.empty();
        private Optional<String> lastName = Optional.empty();
        private Optional<Integer> age = Optional.empty();
        private Optional<Integer> weight = Optional.empty();
        private Optional<Gender> gender = Optional.empty();
        private Optional<Integer> height = Optional.empty();
    }
}
