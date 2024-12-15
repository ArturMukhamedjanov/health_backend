package health.repos;

import health.models.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
    List<Appointment> getAppointmentsByDoctor(Doctor doctor);
    List<Appointment> getAppointmentsByCustomer(Customer customer);
    List<Appointment> getAppointmentsByDoctorAndCustomer(Doctor doctor, Customer customer);
    List<Appointment> getAppointmentsByClinic(Clinic clinic);
    Optional<Appointment> findAppointmentByTimetable(Timetable timetable);
}
