package health.auth.services;

import health.auth.AuthenticationRequest;
import health.auth.AuthenticationResponse;
import health.auth.RegisterRequest;
import health.models.Clinic;
import health.models.Customer;
import health.models.Doctor;
import health.models.auth.Role;
import health.models.auth.User;
import health.repos.ClinicRepo;
import health.repos.CustomerRepo;
import health.repos.DoctorRepo;
import health.repos.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepo userRepo;
    
    @Mock
    private CustomerRepo customerRepo;
    
    @Mock
    private ClinicRepo clinicRepo;
    
    @Mock
    private DoctorRepo doctorRepo;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private Customer customer;
    private Clinic clinic;
    private Doctor doctor;
    private User savedUser;
    private String token;
    private AuthenticationRequest authRequest;

    @BeforeEach
    void setUp() {
        // Setup common test data
        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();
                
        token = "test.jwt.token";
        
        savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded_password")
                .role(Role.CUSTOMER)
                .build();
                
        authRequest = AuthenticationRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();
                
        customer = Customer.builder().build();
        clinic = Clinic.builder().build();
        doctor = Doctor.builder().build();
    }

    @Test
    void registerCustomer_ShouldRegisterCustomerAndReturnToken() {
        // Arrange
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(jwtService.generateToken(any(User.class))).thenReturn(token);
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);
        
        // Act
        AuthenticationResponse response = authenticationService.registerCustomer(registerRequest, customer);
        
        // Assert
        assertEquals(token, response.getToken());
        assertEquals(Role.CUSTOMER, response.getRole());
        
        // Verify that customer was saved with user association
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepo).save(customerCaptor.capture());
        assertEquals(savedUser, customerCaptor.getValue().getUser());
    }

    @Test
    void registerClinic_ShouldRegisterClinicAndReturnToken() {
        // Arrange
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(jwtService.generateToken(any(User.class))).thenReturn(token);
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(clinicRepo.save(any(Clinic.class))).thenReturn(clinic);
        
        // Act
        AuthenticationResponse response = authenticationService.registerClinic(registerRequest, clinic);
        
        // Assert
        assertEquals(token, response.getToken());
        assertEquals(Role.CLINIC, response.getRole());
        
        // Verify that clinic was saved with user association
        ArgumentCaptor<Clinic> clinicCaptor = ArgumentCaptor.forClass(Clinic.class);
        verify(clinicRepo).save(clinicCaptor.capture());
        assertEquals(savedUser, clinicCaptor.getValue().getUser());
    }

    @Test
    void registerDoctor_ShouldRegisterDoctorAndReturnToken() {
        // Arrange
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(jwtService.generateToken(any(User.class))).thenReturn(token);
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(doctorRepo.save(any(Doctor.class))).thenReturn(doctor);
        
        // Act
        AuthenticationResponse response = authenticationService.registerDoctor(registerRequest, doctor);
        
        // Assert
        assertEquals(token, response.getToken());
        assertEquals(Role.DOCTOR, response.getRole());
        
        // Verify that doctor was saved with user association
        ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepo).save(doctorCaptor.capture());
        assertEquals(savedUser, doctorCaptor.getValue().getUser());
    }

    @Test
    void authenticate_ShouldAuthenticateAndReturnToken() {
        // Arrange
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(any(User.class))).thenReturn(token);
        
        // Act
        AuthenticationResponse response = authenticationService.authenticate(authRequest);
        
        // Assert
        assertEquals(token, response.getToken());
        assertEquals(Role.CUSTOMER, response.getRole());
        
        // Verify authentication manager was called
        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void getCurrentUser_ShouldReturnCurrentUser() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepo.getUserByEmail("test@example.com")).thenReturn(savedUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        // Act
        User user = authenticationService.getCurrentUser();
        
        // Assert
        assertEquals(savedUser, user);
    }
}