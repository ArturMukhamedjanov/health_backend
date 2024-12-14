package health.controllers;

import health.auth.services.AuthenticationService;
import health.models.Doctor;
import health.models.dto.DoctorDto;
import health.models.mapper.DoctorMapper;
import health.services.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RequiredArgsConstructor
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final AuthenticationService authenticationService;
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    // Получение информации о докторе для текущего пользователя
    @GetMapping()
    public ResponseEntity<DoctorDto> getDoctor() {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctorDto = doctorMapper.mapToDto(doctor.get());
        doctorDto.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(doctorDto);
    }

    // Обновление информации о докторе
    @PostMapping
    public ResponseEntity<DoctorDto> updateDoctorInfo(@Valid @RequestBody DoctorDto doctorDto) {
        var currentUser = authenticationService.getCurrentUser();
        var currentDoctor = doctorService.getDoctorByUser(currentUser);
        if (currentDoctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var updatedDoctor = mergeDoctors(currentDoctor.get(), doctorDto);
        updatedDoctor = doctorService.updateDoctor(updatedDoctor);
        var res = doctorMapper.mapToDto(updatedDoctor);
        res.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(res);
    }

    public Doctor mergeDoctors(Doctor oldDoctor, DoctorDto newDoctor) {
        if (newDoctor.firstName() != null) {
            oldDoctor.setFirstName(newDoctor.firstName());
        }
        if (newDoctor.lastName() != null) {
            oldDoctor.setLastName(newDoctor.lastName());
        }
        if (newDoctor.speciality() != null) {
            oldDoctor.setSpeciality(newDoctor.speciality());
        }
        return oldDoctor;
    }
}
