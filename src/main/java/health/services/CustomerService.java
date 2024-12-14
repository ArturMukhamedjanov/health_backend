package health.services;

import health.models.Customer;
import health.models.auth.User;
import health.repos.CustomerRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepo customerRepo;

    public Optional<Customer> getCustomerByUser(User currentUser) {
        return customerRepo.findCustomerByUser(currentUser);
    }

    public Customer updateCustomer(Customer updatedCustomer) {
        return customerRepo.save(updatedCustomer);
    }
}
