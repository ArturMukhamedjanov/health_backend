package health.services;

import health.models.Clinic;
import health.models.Doctor;
import health.models.auth.User;
import health.repos.DoctorRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepo doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private User user;
    private Clinic clinic;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .build();
                
        clinic = Clinic.builder()
                .id(1L)
                .name("Test Clinic")
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .speciality("Cardiology")
                .user(user)
                .clinic(clinic)
                .build();
    }

    @Test
    void getDoctorByUser_WhenDoctorExists_ShouldReturnDoctor() {
        // Arrange
        when(doctorRepository.findDoctorByUser(user)).thenReturn(Optional.of(doctor));

        // Act
        Optional<Doctor> result = doctorService.getDoctorByUser(user);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(doctor, result.get());
        verify(doctorRepository).findDoctorByUser(user);
    }

    @Test
    void getDoctorByUser_WhenDoctorDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        User nonExistentUser = User.builder().id(999L).build();
        when(doctorRepository.findDoctorByUser(nonExistentUser)).thenReturn(Optional.empty());

        // Act
        Optional<Doctor> result = doctorService.getDoctorByUser(nonExistentUser);

        // Assert
        assertTrue(result.isEmpty());
        verify(doctorRepository).findDoctorByUser(nonExistentUser);
    }

    @Test
    void updateDoctor_ShouldSaveAndReturnUpdatedDoctor() {
        // Arrange
        Doctor updatedDoctor = Doctor.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("Doctor")
                .speciality("Neurology")
                .user(user)
                .clinic(clinic)
                .build();
        when(doctorRepository.save(updatedDoctor)).thenReturn(updatedDoctor);

        // Act
        Doctor result = doctorService.updateDoctor(updatedDoctor);

        // Assert
        assertEquals(updatedDoctor, result);
        verify(doctorRepository).save(updatedDoctor);
    }

    @Test
    void getDoctorById_WhenDoctorExists_ShouldReturnDoctor() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // Act
        Optional<Doctor> result = doctorService.getDoctorById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(doctor, result.get());
        verify(doctorRepository).findById(1L);
    }

    @Test
    void getDoctorById_WhenDoctorDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Doctor> result = doctorService.getDoctorById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(doctorRepository).findById(999L);
    }

    @Test
    void getDoctorsByClinic_ShouldReturnDoctorsForClinic() {
        // Arrange
        Doctor doctor2 = Doctor.builder()
                .id(2L)
                .firstName("John")
                .lastName("Doe")
                .speciality("Dentistry")
                .clinic(clinic)
                .build();
        List<Doctor> expectedDoctors = Arrays.asList(doctor, doctor2);
        
        when(doctorRepository.getDoctorsByClinic(clinic)).thenReturn(expectedDoctors);

        // Act
        List<Doctor> result = doctorService.getDoctorsByClinic(clinic);

        // Assert
        assertEquals(expectedDoctors, result);
        verify(doctorRepository).getDoctorsByClinic(clinic);
    }

    @Test
    void getAllDoctors_ShouldReturnAllDoctors() {
        // Arrange
        Doctor doctor2 = Doctor.builder()
                .id(2L)
                .firstName("John")
                .lastName("Doe")
                .speciality("Dentistry")
                .build();
        List<Doctor> expectedDoctors = Arrays.asList(doctor, doctor2);
        
        when(doctorRepository.findAll()).thenReturn(expectedDoctors);

        // Act
        List<Doctor> result = doctorService.getAllDoctors();

        // Assert
        assertEquals(expectedDoctors, result);
        verify(doctorRepository).findAll();
    }
}