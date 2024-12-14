package health.services;

import health.models.Doctor;
import health.models.auth.User;
import health.repos.DoctorRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepo doctorRepository;

    public Optional<Doctor> getDoctorByUser(User user) {
        return doctorRepository.findDoctorByUser(user);
    }

    public Doctor updateDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }
}
