package health.models.dto;

import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder(toBuilder = true)
public record DoctorDto(
        Long id,
        Long clinicId,
        Long userId,
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Password is required")
        String password,
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotBlank(message = "Speciality is required")
        String speciality
){

}
