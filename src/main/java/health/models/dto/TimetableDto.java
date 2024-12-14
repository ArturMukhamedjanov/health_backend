package health.models.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record TimetableDto(
        Long id,
        Long doctorId,
        String start,
        Boolean reserved
) {
}
