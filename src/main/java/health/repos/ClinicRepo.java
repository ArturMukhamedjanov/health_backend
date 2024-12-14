package health.repos;

import health.models.Clinic;
import health.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClinicRepo extends JpaRepository<Clinic, Long> {
    Optional<Clinic> findClinicByUser(User user);

}
