package health.services;

import health.models.Analysis;
import health.models.Customer;
import health.repos.AnalysisRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepo analysisRepo;

    public List<Analysis> getAnalysysesByCustomer(Customer customer) {
        return analysisRepo.getAnalysisesByCustomer(customer);
    }

    public List<Analysis> addAnalysis(List<Analysis> analysis) {
        return analysisRepo.saveAll(analysis);
    }

    public Optional<Analysis> getAnalysisById(Long id) {
        return analysisRepo.findById(id);
    }

    public void deleteAnalysis(Analysis analysis) {
        analysisRepo.delete(analysis);
    }
}
