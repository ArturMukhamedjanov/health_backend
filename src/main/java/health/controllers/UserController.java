package health.controllers;

import health.auth.AuthenticationRequest;
import health.auth.AuthenticationResponse;
import health.auth.RegisterRequest;
import health.auth.services.AuthenticationService;
import health.models.Clinic;
import health.models.Customer;
import health.models.dto.ClinicDto;
import health.models.dto.CustomerDto;
import health.models.mapper.ClinicMapper;
import health.models.mapper.CustomerMapper;
import health.services.CustomerService;
import health.services.UserService;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final CustomerMapper customerMapper;
    private final ClinicMapper clinicMapper;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register/customer")
    public ResponseEntity<String> registerCustomer(
            @RequestBody CustomerDto customerDto,
            HttpServletResponse response
    ){
        if(!validateCustomer(customerDto)){
            return ResponseEntity.badRequest().body("Invalid customer data");
        }
        if(userService.getUserByEmail(customerDto.email().get()).isPresent()){
            return ResponseEntity.badRequest().body("User with that email already registered");
        }
        Customer customer = customerMapper.mapFromDto(customerDto);
        var registerRequest = RegisterRequest.builder()
                .email(customerDto.email().get())
                .password(customerDto.password().get())
                .build();
        var authResponse = authenticationService.registerCustomer(registerRequest, customer);
        response.addCookie(createCookie(authResponse.getToken()));
        return ResponseEntity.ok(authResponse.getToken());
    }

    @PostMapping("/register/clinic")
    public ResponseEntity<String> registerClinic(
            @RequestBody ClinicDto clinicDto,
            HttpServletResponse response
    ){
        if(!validateClinic(clinicDto)){
            return ResponseEntity.badRequest().body("Invalid clinic data");
        }
        if(userService.getUserByEmail(clinicDto.email().get()).isPresent()){
            return ResponseEntity.badRequest().body("User with that email already registered");
        }
        Clinic clinic = clinicMapper.fromDto(clinicDto);
        var registerRequest = RegisterRequest.builder()
                .email(clinicDto.email().get())
                .password(clinicDto.password().get())
                .build();
        var authResponse = authenticationService.registerClinic(registerRequest, clinic);
        response.addCookie(createCookie(authResponse.getToken()));
        return ResponseEntity.ok(authResponse.getToken());
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response
    ){
        var authResponse = authenticationService.authenticate(authenticationRequest);
        response.addCookie(createCookie(authResponse.getToken()));
        return ResponseEntity.ok("successfull");
    }

    private boolean validateCustomer(CustomerDto customerDto){
        return customerDto.email().isPresent()
                && customerDto.password().isPresent()
                && customerDto.firstName().isPresent()
                && customerDto.lastName().isPresent();
    }

    private boolean validateClinic(ClinicDto clinicDto){
        return clinicDto.email().isPresent()
                && clinicDto.password().isPresent()
                && clinicDto.name().isPresent();
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
