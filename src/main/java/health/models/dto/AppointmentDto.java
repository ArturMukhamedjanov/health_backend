package health.models.dto;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record AppointmentDto(
        Long id,
        Long clinicId,
        Long doctorId,
        Long customerId,
        Long timetableId,
        Instant start
) {
}
