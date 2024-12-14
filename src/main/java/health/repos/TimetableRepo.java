package health.repos;

import health.models.Doctor;
import health.models.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TimetableRepo extends JpaRepository<Timetable, Long> {

    List<Timetable> getTimetablesByDoctor(Doctor doctor);
    List<Timetable> getTimetablesByDoctorAndReserved(Doctor doctor, boolean reserved);
    Optional<Timetable> findTimetableByDoctorAndStart(Doctor doctor, Instant start);

}
