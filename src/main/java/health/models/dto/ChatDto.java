package health.models.dto;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record ChatDto(
        Long id,
        Long customerId,
        Long doctorId,
        Long clinicId,
        String doctorName,
        String customerName
) {
}
