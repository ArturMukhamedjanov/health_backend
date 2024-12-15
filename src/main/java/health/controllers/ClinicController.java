package health.controllers;

import health.auth.RegisterRequest;
import health.auth.services.AuthenticationService;
import health.models.Clinic;
import health.models.Timetable;
import health.models.auth.Role;
import health.models.dto.AppointmentDto;
import health.models.dto.ClinicDto;
import health.models.dto.DoctorDto;
import health.models.dto.TimetableDto;
import health.models.mapper.AppointmentMapper;
import health.models.mapper.ClinicMapper;
import health.models.mapper.DoctorMapper;
import health.models.mapper.TimetableMapper;
import health.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clinic")
public class ClinicController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ClinicService clinicService;
    private final DoctorService doctorService;
    private final TimetableService timetableService;
    private final AppointmentService appointmentService;
    private final ClinicMapper clinicMapper;
    private final DoctorMapper doctorMapper;
    private final TimetableMapper timetableMapper;
    private final AppointmentMapper appointmentMapper;

    // Получение информации о клинике для текущего пользователя
    @GetMapping()
    public ResponseEntity<ClinicDto> getClinic() {
        var currentUser = authenticationService.getCurrentUser();
        var clinic = clinicService.getClinicByUser(currentUser);
        if (clinic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var clinicDto = clinicMapper.mapToDto(clinic.get());
        clinicDto.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(clinicDto);
    }

    @PostMapping
    public ResponseEntity<ClinicDto> updateClinicInfo(@Valid @RequestBody ClinicDto clinicDto) {
        var currentUser = authenticationService.getCurrentUser();
        var currentClinic = clinicService.getClinicByUser(currentUser);
        if (currentClinic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var updatedClinic = mergeClinics(currentClinic.get(), clinicDto);
        updatedClinic = clinicService.updateClinic(updatedClinic);
        var res = clinicMapper.mapToDto(updatedClinic);
        res.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/doctor")
    public ResponseEntity<LoginResponse> registerDoctor(@Valid @RequestBody DoctorDto doctorDto, HttpServletResponse response){
        var currentUser = authenticationService.getCurrentUser();
        var clinic = clinicService.getClinicByUser(currentUser);
        if (clinic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if(!validateDoctor(doctorDto)){
            return ResponseEntity.badRequest().body(null);
        }
        if(userService.getUserByEmail(doctorDto.email()).isPresent()){
            return ResponseEntity.badRequest().body(null);
        }
        var doctor = doctorMapper.mapFromDto(doctorDto);
        doctor.setClinic(clinic.get());
        var res = authenticationService.registerDoctor(RegisterRequest.builder()
                .email(doctorDto.email())
                .password(doctorDto.password())
                .build(), doctor);
        response.addCookie(createCookie(res.getToken()));
        return ResponseEntity.ok(LoginResponse.builder().role(Role.DOCTOR).build());
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<DoctorDto>> getDoctors(){
        var currentUser = authenticationService.getCurrentUser();
        var clinic = clinicService.getClinicByUser(currentUser);
        if (clinic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctors = doctorService.getDoctorsByClinic(clinic.get());
        var doctorDtos = doctors.stream().map(doctorMapper::mapToDto).toList();
        return ResponseEntity.ok(doctorDtos);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<DoctorDto> getDoctor(@PathVariable Long doctorId){
        var currentUser = authenticationService.getCurrentUser();
        var clinic = clinicService.getClinicByUser(currentUser);
        if (clinic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctorOpt = doctorService.getDoctorById(doctorId);
        if(doctorOpt.isEmpty() || doctorOpt.get().getClinic() != clinic.get()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctorMapper.mapToDto(doctorOpt.get()));
    }

    @GetMapping("/doctor/{doctorId}/timetable")
    public ResponseEntity<List<TimetableDto>> getDoctorTimetable(@PathVariable Long doctorId){
        var currentUser = authenticationService.getCurrentUser();
        var clinic = clinicService.getClinicByUser(currentUser);
        if (clinic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctorOpt = doctorService.getDoctorById(doctorId);
        if(doctorOpt.isEmpty() || doctorOpt.get().getClinic() != clinic.get()){
            return ResponseEntity.notFound().build();
        }
        var timetables = timetableService.getTimetablesByDoctor(doctorOpt.get());
        var timetableDtos = timetables.stream().map(timetableMapper::mapToDto).toList();
        return ResponseEntity.ok(timetableDtos);
    }

    @PostMapping("/doctor/{doctorId}/timetable")
    public ResponseEntity<List<TimetableDto>> setDoctorTimetable(@Valid @RequestBody List<Instant> workingOurs, @PathVariable Long doctorId){
        var currentUser = authenticationService.getCurrentUser();
        var clinic = clinicService.getClinicByUser(currentUser);
        if (clinic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctorOpt = doctorService.getDoctorById(doctorId);
        if(doctorOpt.isEmpty() || doctorOpt.get().getClinic() != clinic.get()){
            return ResponseEntity.notFound().build();
        }
        var reservedTimetables = timetableService.getReservedTimetablesByDoctor(doctorOpt.get());
        Set<Instant> workingOursSet = new HashSet<>(workingOurs);
        reservedTimetables.stream()
                .map(Timetable::getStart) // Получаем все start из reservedTimetables
                .filter(start -> !workingOursSet.contains(start)) // Проверяем, что start нет в workingOursSet
                .forEach(workingOurs::add); // Добавляем отсутствующие элементы в workingOurs
        if(hasOverlappingWorkingHours(workingOurs)){
            return ResponseEntity.badRequest().build();
        }
        timetableService.deleteFreeTimetables(doctorOpt.get());
        List<Timetable> timetables = timetableService.addOrUpdateFromRawTimetable(workingOurs, doctorOpt.get());
        var timetableDtos = timetables.stream().map(timetableMapper::mapToDto).toList();
        return ResponseEntity.ok(timetableDtos);
    }

    @GetMapping("/doctor/{doctorId}/appointment")
    public ResponseEntity<List<AppointmentDto>> getDoctorAppointments(@PathVariable Long doctorId){
        var currentUser = authenticationService.getCurrentUser();
        var clinic = clinicService.getClinicByUser(currentUser);
        if (clinic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctorOpt = doctorService.getDoctorById(doctorId);
        if(doctorOpt.isEmpty() || doctorOpt.get().getClinic() != clinic.get()){
            return ResponseEntity.notFound().build();
        }
        var appointments = appointmentService.getAppointmentsByDoctor(doctorOpt.get());
        var appointmentDtos = appointments.stream().map(appointmentMapper::mapToDto).toList();
        return ResponseEntity.ok(appointmentDtos);
    }

    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDto>> getAppointments(){
        var currentUser = authenticationService.getCurrentUser();
        var clinic = clinicService.getClinicByUser(currentUser);
        if (clinic.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var appointments = appointmentService.getAppointmentsByClinic(clinic.get());
        var appointmentDtos = appointments.stream().map(appointmentMapper::mapToDto).toList();
        return ResponseEntity.ok(appointmentDtos);
    }


    // Метод логики слияния старой информации клиники с новой
    public Clinic mergeClinics(Clinic oldClinic, ClinicDto newClinic) {
        if (newClinic.name() != null) {
            oldClinic.setName(newClinic.name());
        }
        if (newClinic.description() != null) {
            oldClinic.setDescription(newClinic.description());
        }
        return oldClinic;
    }

    public boolean validateDoctor(DoctorDto doctorDto){
        return doctorDto.email() != null &&
                doctorDto.password() != null &&
                doctorDto.firstName() != null &&
                doctorDto.lastName() != null &&
                doctorDto.speciality() != null;
    }

    public static boolean hasOverlappingWorkingHours(List<Instant> workingHours) {
        if (workingHours == null || workingHours.size() <= 1) {
            return false;
        }
        workingHours.sort(Instant::compareTo);
        for (int i = 0; i < workingHours.size() - 1; i++) {
            Instant current = workingHours.get(i);
            Instant next = workingHours.get(i + 1);
            if (Duration.between(current, next).toMinutes() < 60) {
                return true;
            }
        }
        return false;
    }

    private Cookie createCookie(String token){
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        return cookie;
    }

}
