package health.auth.services;

import health.models.Clinic;
import health.models.Customer;
import health.repos.ClinicRepo;
import health.repos.CustomerRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import health.auth.AuthenticationRequest;
import health.auth.AuthenticationResponse;
import health.auth.RegisterRequest;
import health.models.auth.Role;
import health.models.auth.User;
import health.models.auth.User.UserBuilder;
import health.repos.UserRepo;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepo userRepo;
    private final CustomerRepo customerRepo;
    private final ClinicRepo clinicRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse registerCustomer(RegisterRequest request, Customer customer) {
        User user =  registerUser(request, Role.CUSTOMER);
        String jwtToken = jwtService.generateToken(user);
        customer.setUser(user);
        customerRepo.save(customer);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

   public AuthenticationResponse registerClinic(RegisterRequest request, Clinic clinic) {
       User user = registerUser(request, Role.CLINIC);
       String jwtToken = jwtService.generateToken(user);
       clinic.setUser(user);
       clinicRepo.save(clinic);
       return AuthenticationResponse.builder()
               .token(jwtToken)
               .build();
    }

    public AuthenticationResponse registerDoctor(RegisterRequest request) {
        User user =  registerUser(request, Role.DOCTOR);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public User registerUser(RegisterRequest request, Role role){
        UserBuilder userBuilder = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role);
        User user = userBuilder.build();
        user = userRepo.save(user);
        return user;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(null);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }

    public User getCurrentUser(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.getUserByEmail(userDetails.getUsername());
    }
}
