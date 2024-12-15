package health.services;

import health.models.*;
import health.repos.AppointmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepo appointmentRepository;

    public List<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        return appointmentRepository.getAppointmentsByDoctor(doctor);
    }

    public List<Appointment> getAppointmentsByCustomer(Customer customer) {
        return appointmentRepository.getAppointmentsByCustomer(customer);
    }

    public List<Appointment> getAppointmentsByDoctorAndCustomer(Doctor doctor, Customer customer) {
        return appointmentRepository.getAppointmentsByDoctorAndCustomer(doctor, customer);
    }

    public List<Appointment> getAppointmentsByClinic(Clinic clinic) {
        return appointmentRepository.getAppointmentsByClinic(clinic);
    }

    public Optional<Appointment> getAppointmentByTimetable(Timetable timetable) {
        return appointmentRepository.findAppointmentByTimetable(timetable);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment saveOrUpdateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }
}
