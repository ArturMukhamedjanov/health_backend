package health.services;

import health.models.Clinic;
import health.models.auth.User;
import health.repos.ClinicRepo;
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
class ClinicServiceTest {

    @Mock
    private ClinicRepo clinicRepository;

    @InjectMocks
    private ClinicService clinicService;

    private Clinic clinic;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("clinic@example.com")
                .build();

        clinic = Clinic.builder()
                .id(1L)
                .name("Medical Center")
                .description("Comprehensive medical services")
                .user(user)
                .build();
    }

    @Test
    void getClinicById_WhenClinicExists_ShouldReturnClinic() {
        // Arrange
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        // Act
        Optional<Clinic> result = clinicService.getClinicById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(clinic, result.get());
        verify(clinicRepository).findById(1L);
    }

    @Test
    void getClinicById_WhenClinicDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(clinicRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Clinic> result = clinicService.getClinicById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(clinicRepository).findById(999L);
    }

    @Test
    void getClinicByUser_WhenClinicExists_ShouldReturnClinic() {
        // Arrange
        when(clinicRepository.findClinicByUser(user)).thenReturn(Optional.of(clinic));

        // Act
        Optional<Clinic> result = clinicService.getClinicByUser(user);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(clinic, result.get());
        verify(clinicRepository).findClinicByUser(user);
    }

    @Test
    void getClinicByUser_WhenClinicDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        User nonExistentUser = User.builder().id(999L).build();
        when(clinicRepository.findClinicByUser(nonExistentUser)).thenReturn(Optional.empty());

        // Act
        Optional<Clinic> result = clinicService.getClinicByUser(nonExistentUser);

        // Assert
        assertTrue(result.isEmpty());
        verify(clinicRepository).findClinicByUser(nonExistentUser);
    }

    @Test
    void updateClinic_ShouldSaveAndReturnUpdatedClinic() {
        // Arrange
        Clinic updatedClinic = Clinic.builder()
                .id(1L)
                .name("Updated Medical Center")
                .description("New services available")
                .user(user)
                .build();
        when(clinicRepository.save(updatedClinic)).thenReturn(updatedClinic);

        // Act
        Clinic result = clinicService.updateClinic(updatedClinic);

        // Assert
        assertEquals(updatedClinic, result);
        verify(clinicRepository).save(updatedClinic);
    }

    @Test
    void getAllClinics_ShouldReturnAllClinics() {
        // Arrange
        Clinic clinic2 = Clinic.builder()
                .id(2L)
                .name("Dental Clinic")
                .description("Specialized dental services")
                .build();
        List<Clinic> expectedClinics = Arrays.asList(clinic, clinic2);
        
        when(clinicRepository.findAll()).thenReturn(expectedClinics);

        // Act
        List<Clinic> result = clinicService.getAllClinics();

        // Assert
        assertEquals(expectedClinics, result);
        verify(clinicRepository).findAll();
    }
}