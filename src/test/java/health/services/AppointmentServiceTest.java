package health.services;

import health.models.*;
import health.repos.AppointmentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepo appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Doctor doctor;
    private Customer customer;
    private Clinic clinic;
    private Timetable timetable;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        clinic = Clinic.builder()
                .id(1L)
                .name("Test Clinic")
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .speciality("Cardiology")
                .clinic(clinic)
                .build();

        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .build();

        timetable = Timetable.builder()
                .id(1L)
                .doctor(doctor)
                .start(Instant.now())
                .reserved(true)
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .customer(customer)
                .clinic(clinic)
                .timetable(timetable)
                .build();
    }

    @Test
    void getAppointmentsByDoctor_ShouldReturnAppointmentsForDoctor() {
        // Arrange
        List<Appointment> expectedAppointments = List.of(appointment);
        when(appointmentRepository.getAppointmentsByDoctor(doctor)).thenReturn(expectedAppointments);

        // Act
        List<Appointment> result = appointmentService.getAppointmentsByDoctor(doctor);

        // Assert
        assertEquals(expectedAppointments, result);
        verify(appointmentRepository).getAppointmentsByDoctor(doctor);
    }

    @Test
    void getAppointmentsByCustomer_ShouldReturnAppointmentsForCustomer() {
        // Arrange
        List<Appointment> expectedAppointments = List.of(appointment);
        when(appointmentRepository.getAppointmentsByCustomer(customer)).thenReturn(expectedAppointments);

        // Act
        List<Appointment> result = appointmentService.getAppointmentsByCustomer(customer);

        // Assert
        assertEquals(expectedAppointments, result);
        verify(appointmentRepository).getAppointmentsByCustomer(customer);
    }

    @Test
    void getAppointmentsByDoctorAndCustomer_ShouldReturnAppointmentsForDoctorAndCustomer() {
        // Arrange
        List<Appointment> expectedAppointments = List.of(appointment);
        when(appointmentRepository.getAppointmentsByDoctorAndCustomer(doctor, customer))
                .thenReturn(expectedAppointments);

        // Act
        List<Appointment> result = appointmentService.getAppointmentsByDoctorAndCustomer(doctor, customer);

        // Assert
        assertEquals(expectedAppointments, result);
        verify(appointmentRepository).getAppointmentsByDoctorAndCustomer(doctor, customer);
    }

    @Test
    void getAppointmentsByClinic_ShouldReturnAppointmentsForClinic() {
        // Arrange
        List<Appointment> expectedAppointments = List.of(appointment);
        when(appointmentRepository.getAppointmentsByClinic(clinic)).thenReturn(expectedAppointments);

        // Act
        List<Appointment> result = appointmentService.getAppointmentsByClinic(clinic);

        // Assert
        assertEquals(expectedAppointments, result);
        verify(appointmentRepository).getAppointmentsByClinic(clinic);
    }

    @Test
    void getAppointmentByTimetable_WhenAppointmentExists_ShouldReturnAppointment() {
        // Arrange
        when(appointmentRepository.findAppointmentByTimetable(timetable))
                .thenReturn(Optional.of(appointment));

        // Act
        Optional<Appointment> result = appointmentService.getAppointmentByTimetable(timetable);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(appointment, result.get());
        verify(appointmentRepository).findAppointmentByTimetable(timetable);
    }

    @Test
    void getAppointmentById_WhenAppointmentExists_ShouldReturnAppointment() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        // Act
        Optional<Appointment> result = appointmentService.getAppointmentById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(appointment, result.get());
        verify(appointmentRepository).findById(1L);
    }

    @Test
    void saveOrUpdateAppointment_ShouldSaveAndReturnAppointment() {
        // Arrange
        Appointment newAppointment = Appointment.builder()
                .doctor(doctor)
                .customer(customer)
                .clinic(clinic)
                .timetable(timetable)
                .build();
                
        when(appointmentRepository.save(newAppointment)).thenReturn(
                Appointment.builder()
                        .id(2L)
                        .doctor(doctor)
                        .customer(customer)
                        .clinic(clinic)
                        .timetable(timetable)
                        .build());

        // Act
        Appointment result = appointmentService.saveOrUpdateAppointment(newAppointment);

        // Assert
        assertEquals(2L, result.getId());
        assertEquals(doctor, result.getDoctor());
        assertEquals(customer, result.getCustomer());
        assertEquals(clinic, result.getClinic());
        assertEquals(timetable, result.getTimetable());
        verify(appointmentRepository).save(newAppointment);
    }

    @Test
    void deleteAppointment_ShouldDeleteAppointment() {
        // Act
        appointmentService.deleteAppointment(appointment);

        // Assert
        verify(appointmentRepository).delete(appointment);
    }
}