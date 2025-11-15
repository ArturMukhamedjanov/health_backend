package health.services;

import health.models.Analysis;
import health.models.Customer;
import health.repos.AnalysisRepo;
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
class AnalysisServiceTest {

    @Mock
    private AnalysisRepo analysisRepo;

    @InjectMocks
    private AnalysisService analysisService;

    private Customer customer;
    private Analysis analysis1;
    private Analysis analysis2;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        analysis1 = Analysis.builder()
                .id(1L)
                .customer(customer)
                .name("Glucose")
                .value("5.5")
                .unit("mmol/L")
                .date(Instant.now().minusSeconds(86400)) // yesterday
                .build();

        analysis2 = Analysis.builder()
                .id(2L)
                .customer(customer)
                .name("Cholesterol")
                .value("4.2")
                .unit("mmol/L")
                .date(Instant.now())
                .build();
    }

    @Test
    void getAnalysysesByCustomer_ShouldReturnAnalysesForCustomer() {
        // Arrange
        List<Analysis> expectedAnalyses = Arrays.asList(analysis1, analysis2);
        when(analysisRepo.getAnalysisesByCustomer(customer)).thenReturn(expectedAnalyses);

        // Act
        List<Analysis> result = analysisService.getAnalysysesByCustomer(customer);

        // Assert
        assertEquals(expectedAnalyses, result);
        verify(analysisRepo).getAnalysisesByCustomer(customer);
    }

    @Test
    void addAnalysis_ShouldSaveAndReturnAnalyses() {
        // Arrange
        List<Analysis> analysesToAdd = Arrays.asList(analysis1, analysis2);
        when(analysisRepo.saveAll(analysesToAdd)).thenReturn(analysesToAdd);

        // Act
        List<Analysis> result = analysisService.addAnalysis(analysesToAdd);

        // Assert
        assertEquals(analysesToAdd, result);
        verify(analysisRepo).saveAll(analysesToAdd);
    }

    @Test
    void getAnalysisById_WhenAnalysisExists_ShouldReturnAnalysis() {
        // Arrange
        when(analysisRepo.findById(1L)).thenReturn(Optional.of(analysis1));

        // Act
        Optional<Analysis> result = analysisService.getAnalysisById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(analysis1, result.get());
        verify(analysisRepo).findById(1L);
    }

    @Test
    void getAnalysisById_WhenAnalysisDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(analysisRepo.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Analysis> result = analysisService.getAnalysisById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(analysisRepo).findById(999L);
    }

    @Test
    void deleteAnalysis_ShouldDeleteAnalysis() {
        // Act
        analysisService.deleteAnalysis(analysis1);

        // Assert
        verify(analysisRepo).delete(analysis1);
    }
}