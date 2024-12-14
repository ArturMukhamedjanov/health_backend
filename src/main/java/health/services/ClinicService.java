package health.services;

import health.models.Clinic;
import health.models.auth.User;
import health.repos.ClinicRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepo clinicRepository;

    public Optional<Clinic> getClinicByUser(User user) {
        return clinicRepository.findClinicByUser(user);
    }

    public Clinic updateClinic(Clinic clinic) {
        return clinicRepository.save(clinic);
    }
}
