package health.controllers;

import health.auth.services.AuthenticationService;
import health.models.Doctor;
import health.models.dto.AppointmentDto;
import health.models.dto.DoctorDto;
import health.models.dto.TimetableDto;
import health.models.mapper.AppointmentMapper;
import health.models.mapper.DoctorMapper;
import health.models.mapper.TimetableMapper;
import health.services.AppointmentService;
import health.services.DoctorService;
import health.services.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final AuthenticationService authenticationService;
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;
    private final TimetableService timetableService;
    private final TimetableMapper timetableMapper;
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

    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDto>> getAppointments() {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var appointments = appointmentService.getAppointmentsByDoctor(doctor.get());
        var appointmentDtos = appointments.stream().map(appointmentMapper::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDtos);
    }

    @GetMapping("/timetable")
    public ResponseEntity<List<TimetableDto>> getTimetable() {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var timetable = timetableService.getTimetablesByDoctor(doctor.get());
        var timetableDtos = timetable.stream().map(timetableMapper::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(timetableDtos);
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
