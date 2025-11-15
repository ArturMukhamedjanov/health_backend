package health.services;

import health.models.Doctor;
import health.models.Timetable;
import health.repos.TimetableRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class TimetableServiceTest {

    @Mock
    private TimetableRepo timetableRepository;

    @InjectMocks
    private TimetableService timetableService;

    private Doctor doctor;
    private Timetable timetable1;
    private Timetable timetable2;
    private Instant now;
    private Instant hourLater;

    @BeforeEach
    void setUp() {
        now = Instant.now();
        hourLater = now.plusSeconds(3600);
        
        doctor = Doctor.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .speciality("Cardiology")
                .build();

        timetable1 = Timetable.builder()
                .id(1L)
                .doctor(doctor)
                .start(now)
                .reserved(false)
                .build();

        timetable2 = Timetable.builder()
                .id(2L)
                .doctor(doctor)
                .start(hourLater)
                .reserved(true)
                .build();
    }

    @Test
    void getTimetablesByDoctor_ShouldReturnTimetablesForDoctor() {
        // Arrange
        List<Timetable> expectedTimetables = Arrays.asList(timetable1, timetable2);
        when(timetableRepository.getTimetablesByDoctor(doctor)).thenReturn(expectedTimetables);

        // Act
        List<Timetable> result = timetableService.getTimetablesByDoctor(doctor);

        // Assert
        assertEquals(expectedTimetables, result);
        verify(timetableRepository).getTimetablesByDoctor(doctor);
    }

    @Test
    void getFreeTimetablesByDoctor_ShouldReturnFreeTimetablesOnly() {
        // Arrange
        when(timetableRepository.getTimetablesByDoctorAndReserved(doctor, false))
                .thenReturn(List.of(timetable1));

        // Act
        List<Timetable> result = timetableService.getFreeTimetablesByDoctor(doctor);

        // Assert
        assertEquals(1, result.size());
        assertEquals(timetable1, result.get(0));
        verify(timetableRepository).getTimetablesByDoctorAndReserved(doctor, false);
    }

    @Test
    void getReservedTimetablesByDoctor_ShouldReturnReservedTimetablesOnly() {
        // Arrange
        when(timetableRepository.getTimetablesByDoctorAndReserved(doctor, true))
                .thenReturn(List.of(timetable2));

        // Act
        List<Timetable> result = timetableService.getReservedTimetablesByDoctor(doctor);

        // Assert
        assertEquals(1, result.size());
        assertEquals(timetable2, result.get(0));
        verify(timetableRepository).getTimetablesByDoctorAndReserved(doctor, true);
    }

    @Test
    void getTimetableByDoctorAndStart_WhenTimetableExists_ShouldReturnTimetable() {
        // Arrange
        when(timetableRepository.findTimetableByDoctorAndStart(doctor, now))
                .thenReturn(Optional.of(timetable1));

        // Act
        Optional<Timetable> result = timetableService.getTimetableByDoctorAndStart(doctor, now);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(timetable1, result.get());
        verify(timetableRepository).findTimetableByDoctorAndStart(doctor, now);
    }

    @Test
    void getTimetableById_WhenTimetableExists_ShouldReturnTimetable() {
        // Arrange
        when(timetableRepository.findById(1L)).thenReturn(Optional.of(timetable1));

        // Act
        Optional<Timetable> result = timetableService.getTimetableById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(timetable1, result.get());
        verify(timetableRepository).findById(1L);
    }

    @Test
    void createTimetable_ShouldSaveAndReturnNewTimetable() {
        // Arrange
        Timetable newTimetable = Timetable.builder()
                .doctor(doctor)
                .start(now.plusSeconds(7200))
                .reserved(false)
                .build();
        
        when(timetableRepository.save(newTimetable)).thenReturn(
                Timetable.builder()
                        .id(3L)
                        .doctor(doctor)
                        .start(now.plusSeconds(7200))
                        .reserved(false)
                        .build());

        // Act
        Timetable result = timetableService.createTimetable(newTimetable);

        // Assert
        assertEquals(3L, result.getId());
        assertEquals(doctor, result.getDoctor());
        assertEquals(now.plusSeconds(7200), result.getStart());
        assertFalse(result.isReserved());
        verify(timetableRepository).save(newTimetable);
    }

    @Test
    void updateTimetable_ShouldSaveAndReturnUpdatedTimetable() {
        // Arrange
        Timetable updatedTimetable = Timetable.builder()
                .id(1L)
                .doctor(doctor)
                .start(now.plusSeconds(1800))
                .reserved(true)
                .build();
        
        when(timetableRepository.save(updatedTimetable)).thenReturn(updatedTimetable);

        // Act
        Timetable result = timetableService.updateTimetable(updatedTimetable);

        // Assert
        assertEquals(updatedTimetable, result);
        verify(timetableRepository).save(updatedTimetable);
    }

    @Test
    void deleteTimetable_ShouldDeleteTimetable() {
        // Act
        timetableService.deleteTimetable(timetable1);

        // Assert
        verify(timetableRepository).delete(timetable1);
    }

    @Test
    void reserveTimetable_ShouldSetReservedToTrueAndSave() {
        // Arrange
        ArgumentCaptor<Timetable> timetableCaptor = ArgumentCaptor.forClass(Timetable.class);
        when(timetableRepository.save(any(Timetable.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        timetableService.reserveTimetable(timetable1);

        // Assert
        verify(timetableRepository).save(timetableCaptor.capture());
        Timetable savedTimetable = timetableCaptor.getValue();
        
        assertEquals(timetable1.getId(), savedTimetable.getId());
        assertTrue(savedTimetable.isReserved());
    }

    @Test
    void freeTimetable_ShouldSetReservedToFalseAndSave() {
        // Arrange
        ArgumentCaptor<Timetable> timetableCaptor = ArgumentCaptor.forClass(Timetable.class);
        when(timetableRepository.save(any(Timetable.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        timetableService.freeTimetable(timetable2);

        // Assert
        verify(timetableRepository).save(timetableCaptor.capture());
        Timetable savedTimetable = timetableCaptor.getValue();
        
        assertEquals(timetable2.getId(), savedTimetable.getId());
        assertFalse(savedTimetable.isReserved());
    }

    @Test
    void deleteFreeTimetables_ShouldDeleteOnlyFreeTimetables() {
        // Arrange
        List<Timetable> freeTimetables = List.of(timetable1);
        when(timetableRepository.getTimetablesByDoctorAndReserved(doctor, false))
                .thenReturn(freeTimetables);

        // Act
        timetableService.deleteFreeTimetables(doctor);

        // Assert
        verify(timetableRepository).getTimetablesByDoctorAndReserved(doctor, false);
        verify(timetableRepository).deleteAll(freeTimetables);
    }

    @Test
    void addOrUpdateFromRawTimetable_WithExistingTimetable_ShouldNotCreateNewTimetable() {
        // Arrange
        List<Instant> workingHours = List.of(now);
        when(timetableRepository.findTimetableByDoctorAndStart(doctor, now))
                .thenReturn(Optional.of(timetable1));

        // Act
        List<Timetable> result = timetableService.addOrUpdateFromRawTimetable(workingHours, doctor);

        // Assert
        assertEquals(1, result.size());
        assertEquals(timetable1, result.get(0));
        verify(timetableRepository, never()).save(any(Timetable.class));
    }

    @Test
    void addOrUpdateFromRawTimetable_WithNewTimetable_ShouldCreateNewTimetable() {
        // Arrange
        Instant newTime = now.plusSeconds(10800); // 3 hours later
        List<Instant> workingHours = List.of(newTime);
        
        when(timetableRepository.findTimetableByDoctorAndStart(doctor, newTime))
                .thenReturn(Optional.empty());
                
        when(timetableRepository.save(any(Timetable.class)))
                .thenAnswer(invocation -> {
                    Timetable saved = invocation.getArgument(0);
                    return Timetable.builder()
                            .id(3L)
                            .doctor(saved.getDoctor())
                            .start(saved.getStart())
                            .reserved(saved.isReserved())
                            .build();
                });

        // Act
        List<Timetable> result = timetableService.addOrUpdateFromRawTimetable(workingHours, doctor);

        // Assert
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(doctor, result.get(0).getDoctor());
        assertEquals(newTime, result.get(0).getStart());
        assertFalse(result.get(0).isReserved());
        
        verify(timetableRepository).save(argThat(timetable -> 
                timetable.getDoctor().equals(doctor) &&
                timetable.getStart().equals(newTime) &&
                !timetable.isReserved()));
    }
}