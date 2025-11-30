package health.models.dto;

import lombok.Builder;

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
