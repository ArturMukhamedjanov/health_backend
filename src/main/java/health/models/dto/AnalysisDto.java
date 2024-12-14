package health.models.dto;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record AnalysisDto(
        Long id,
        Long customerId,
        String name,
        String value,
        Instant date
) {
}
