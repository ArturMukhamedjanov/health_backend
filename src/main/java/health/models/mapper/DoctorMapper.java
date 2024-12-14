package health.models.mapper;

import health.models.Doctor;
import health.models.dto.DoctorDto;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    // Преобразование из DTO в модель Doctor
    public Doctor mapFromDto(DoctorDto dto) {
        var builder = Doctor.builder();

        if (dto.firstName() != null) {
            builder.firstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            builder.lastName(dto.lastName());
        }
        if (dto.speciality() != null) {
            builder.speciality(dto.speciality());
        }
        return builder.build();
    }

    // Преобразование из модели Doctor в DTO
    public DoctorDto mapToDto(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getId())
                .userId(doctor.getUser().getId())
                .email(doctor.getUser().getEmail())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .speciality(doctor.getSpeciality())
                .build();
    }
}
