package health.models.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record ClinicDto (
        Long id,
        String email,
        String password,
        Long userId,
        String name,
        String description
){
}
