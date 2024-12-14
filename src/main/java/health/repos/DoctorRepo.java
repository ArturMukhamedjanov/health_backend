package health.repos;

import health.models.Clinic;
import health.models.Doctor;
import health.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepo extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findDoctorByUser(User user);
    List<Doctor> getDoctorsByClinic(Clinic clinic);
}
