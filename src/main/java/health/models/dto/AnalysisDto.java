package health.models.dto;

import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Builder(toBuilder = true)
public record AnalysisDto(
        Long id,
        Long customerId,
        @NotBlank(message = "Analysis name is required")
        String name,
        @NotBlank(message = "Analysis value is required")
        String value,
        @NotBlank(message = "Analysis unit is required")
        String unit,
        @NotNull(message = "Analysis date is required")
        Instant date
) {
}
