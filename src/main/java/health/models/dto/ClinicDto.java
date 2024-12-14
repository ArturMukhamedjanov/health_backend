package health.models.dto;

import lombok.Builder;

import java.util.Optional;

@Builder(toBuilder = true)
public record ClinicDto (
        Optional<Long> id,
        Optional<String> email,
        Optional<String> password,
        Optional<Long> userId,
        Optional<String> name,
        Optional<String> description
){
    public static class ClinicDtoBuilder {
        private Optional<Long> id = Optional.empty();
        private Optional<Long> userId = Optional.empty();
        private Optional<String> name = Optional.empty();
        private Optional<String> description = Optional.empty();
    }
}
