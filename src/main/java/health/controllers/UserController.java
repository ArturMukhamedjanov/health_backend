package health.controllers;

import health.auth.AuthenticationRequest;
import health.auth.RegisterRequest;
import health.auth.services.AuthenticationService;
import health.models.Clinic;
import health.models.Customer;
import health.models.auth.Role;
import health.models.dto.ClinicDto;
import health.models.dto.CustomerDto;
import health.models.mapper.ClinicMapper;
import health.models.mapper.CustomerMapper;
import health.services.UserService;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final CustomerMapper customerMapper;
    private final ClinicMapper clinicMapper;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register/customer")
    public ResponseEntity<LoginResponse> registerCustomer(
            @Valid @RequestBody CustomerDto customerDto,
            HttpServletResponse response
    ){
        if(!validateCustomer(customerDto)){
            return ResponseEntity.badRequest().body(null);
        }
        if(userService.getUserByEmail(customerDto.email()).isPresent()){
            return ResponseEntity.badRequest().body(null);
        }
        Customer customer = customerMapper.mapFromDto(customerDto);
        var registerRequest = RegisterRequest.builder()
                .email(customerDto.email())
                .password(customerDto.password())
                .build();
        var authResponse = authenticationService.registerCustomer(registerRequest, customer);
        response.addCookie(createCookie(authResponse.getToken()));
        return ResponseEntity.ok(LoginResponse.builder().role(Role.CUSTOMER).build());
    }

    @PostMapping("/register/clinic")
    public ResponseEntity<LoginResponse> registerClinic(
            @Valid @RequestBody ClinicDto clinicDto,
            HttpServletResponse response
    ){
        if(!validateClinic(clinicDto)){
            return ResponseEntity.badRequest().body(null);
        }
        if(userService.getUserByEmail(clinicDto.email()).isPresent()){
            return ResponseEntity.badRequest().body(null);
        }
        Clinic clinic = clinicMapper.mapFromDto(clinicDto);
        var registerRequest = RegisterRequest.builder()
                .email(clinicDto.email())
                .password(clinicDto.password())
                .build();
        var authResponse = authenticationService.registerClinic(registerRequest, clinic);
        response.addCookie(createCookie(authResponse.getToken()));
        return ResponseEntity.ok(LoginResponse.builder().role(Role.CLINIC).build());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response
    ){
        var authResponse = authenticationService.authenticate(authenticationRequest);
        response.addCookie(createCookie(authResponse.getToken()));
        return ResponseEntity.ok(LoginResponse.builder().role(authResponse.getRole()).build());
    }

    private boolean validateCustomer(CustomerDto customerDto){
        return customerDto.email()!= null
                && customerDto.password() != null
                && customerDto.firstName() != null
                && customerDto.lastName() != null;
    }

    private boolean validateClinic(ClinicDto clinicDto){
        return clinicDto.email() != null
                && clinicDto.password() != null
                && clinicDto.name() != null;
    }

    private Cookie createCookie(String token){
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        return cookie;
    }
}
