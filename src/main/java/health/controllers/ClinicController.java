package health.controllers;

import health.auth.RegisterRequest;
import health.auth.services.AuthenticationService;
import health.models.Clinic;
import health.models.auth.Role;
import health.models.dto.ClinicDto;
import health.models.dto.DoctorDto;
import health.models.mapper.ClinicMapper;
import health.models.mapper.DoctorMapper;
import health.services.ClinicService;
import health.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clinic")
public class ClinicController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ClinicService clinicService;
    private final ClinicMapper clinicMapper;
    private final DoctorMapper doctorMapper;

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

    private Cookie createCookie(String token){
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        return cookie;
    }

}
