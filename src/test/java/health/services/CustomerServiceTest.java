package health.services;

import health.models.Customer;
import health.models.auth.User;
import health.repos.CustomerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepo customerRepo;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("customer@example.com")
                .build();

        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .user(user)
                .build();
    }

    @Test
    void getCustomerById_WhenCustomerExists_ShouldReturnCustomer() {
        // Arrange
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        // Act
        Optional<Customer> result = customerService.getCustomerById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepo).findById(1L);
    }

    @Test
    void getCustomerById_WhenCustomerDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(customerRepo.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.getCustomerById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(customerRepo).findById(999L);
    }

    @Test
    void getCustomerByUser_WhenCustomerExists_ShouldReturnCustomer() {
        // Arrange
        when(customerRepo.findCustomerByUser(user)).thenReturn(Optional.of(customer));

        // Act
        Optional<Customer> result = customerService.getCustomerByUser(user);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepo).findCustomerByUser(user);
    }

    @Test
    void getCustomerByUser_WhenCustomerDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        User nonExistentUser = User.builder().id(999L).build();
        when(customerRepo.findCustomerByUser(nonExistentUser)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.getCustomerByUser(nonExistentUser);

        // Assert
        assertTrue(result.isEmpty());
        verify(customerRepo).findCustomerByUser(nonExistentUser);
    }

    @Test
    void updateCustomer_ShouldSaveAndReturnUpdatedCustomer() {
        // Arrange
        Customer updatedCustomer = Customer.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("Customer")
                .age(35)
                .user(user)
                .build();
        when(customerRepo.save(updatedCustomer)).thenReturn(updatedCustomer);

        // Act
        Customer result = customerService.updateCustomer(updatedCustomer);

        // Assert
        assertEquals(updatedCustomer, result);
        verify(customerRepo).save(updatedCustomer);
    }
}