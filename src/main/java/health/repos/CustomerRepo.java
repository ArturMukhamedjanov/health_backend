package health.repos;

import health.models.Customer;
import health.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerByUser(User user);
}
