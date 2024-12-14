package health.repos;

import health.models.Analysis;
import health.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRepo extends JpaRepository<Analysis, Long> {

    List<Analysis> getAnalysisesByCustomer(Customer customer);



}
