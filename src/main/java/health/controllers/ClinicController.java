package health.controllers;

import health.auth.services.AuthenticationService;
import health.models.Clinic;
import health.models.dto.ClinicDto;
import health.models.mapper.ClinicMapper;
import health.services.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clinic")
public class ClinicController {

    private final AuthenticationService authenticationService;
    private final ClinicService clinicService;
    private final ClinicMapper clinicMapper;

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

}
