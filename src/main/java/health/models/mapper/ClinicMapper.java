package health.models.mapper;

import health.models.Clinic;
import health.models.dto.ClinicDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClinicMapper {

    public Clinic fromDto(ClinicDto dto){
        var builder =  Clinic.builder();
        dto.name().ifPresent(builder::name);
        dto.description().ifPresent(builder::description);
        return builder.build();
    }

    public ClinicDto toDto(Clinic clinic){
        return ClinicDto.builder()
                .id(Optional.of(clinic.getId()))
                .userId(Optional.of(clinic.getUser().getId()))
                .name(Optional.ofNullable(clinic.getName()))
                .description(Optional.ofNullable(clinic.getDescription()))
                .build();
    }
}
