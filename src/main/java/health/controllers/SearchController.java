package health.controllers;

import health.models.Clinic;
import health.models.Doctor;
import health.models.dto.ClinicDto;
import health.models.dto.DoctorDto;
import health.models.dto.TimetableDto;
import health.models.mapper.ClinicMapper;
import health.models.mapper.DoctorMapper;
import health.models.mapper.TimetableMapper;
import health.services.ClinicService;
import health.services.DoctorService;
import health.services.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final ClinicService clinicService;
    private final ClinicMapper clinicMapper;
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final TimetableService timetableService;
    private final TimetableMapper timetableMapper;

    @GetMapping("/clinic")
    public ResponseEntity<List<ClinicDto>> getAllClinics() {
        List<Clinic> clinics = clinicService.getAllClinics();
        List<ClinicDto> clinicDtos = clinics.stream().map(clinicMapper::mapToDto).toList();
        return ResponseEntity.ok(clinicDtos);
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        List<DoctorDto> doctorDtos = doctors.stream().map(doctorMapper::mapToDto).toList();
        return ResponseEntity.ok(doctorDtos);
    }

    @GetMapping("/clinic/{clinicId}/doctor")
    public ResponseEntity<List<DoctorDto>> getDoctorsByClinic(@PathVariable Long clinicId) {
        var clinicOpt = clinicService.getClinicById(clinicId);
        if (clinicOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctors = doctorService.getDoctorsByClinic(clinicOpt.get());
        var doctorDtos = doctors.stream().map(doctorMapper::mapToDto).toList();
        return ResponseEntity.ok(doctorDtos);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long doctorId) {
        var doctorOpt = doctorService.getDoctorById(doctorId);
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctorDto = doctorMapper.mapToDto(doctorOpt.get());
        return ResponseEntity.ok(doctorDto);
    }

    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<ClinicDto> getClinicById(@PathVariable Long clinicId) {
        var clinicOpt = clinicService.getClinicById(clinicId);
        if (clinicOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var clinicDto = clinicMapper.mapToDto(clinicOpt.get());
        return ResponseEntity.ok(clinicDto);
    }

    @GetMapping("/doctor/{doctorId}/timetable")
    public ResponseEntity<List<TimetableDto>> getDoctorTimetable(@PathVariable Long doctorId) {
        var doctorOpt = doctorService.getDoctorById(doctorId);
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var timetables = timetableService.getTimetablesByDoctor(doctorOpt.get());
        var timetableDtos = timetables.stream().map(timetableMapper::mapToDto).toList();
        return ResponseEntity.ok(timetableDtos);
    }
}
