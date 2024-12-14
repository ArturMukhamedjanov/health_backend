package health.controllers;

import health.auth.services.AuthenticationService;
import health.models.Analysis;
import health.models.Customer;
import health.models.dto.AnalysisDto;
import health.models.dto.CustomerDto;
import health.models.mapper.AnalysisMapper;
import health.models.mapper.CustomerMapper;
import health.services.AnalysisService;
import health.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final AuthenticationService authenticationService;
    private final CustomerService customerService;
    private final AnalysisService analysisService;
    private final AnalysisMapper analysisMapper;
    private final CustomerMapper customerMapper;


    @GetMapping()
    public ResponseEntity<CustomerDto> getCustomer() {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var customerDto = customerMapper.mapToDto(customer.get());
        customerDto.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(customerDto);
    }

    @PostMapping
    public ResponseEntity<CustomerDto> updateCustomerInfo(@Valid @RequestBody CustomerDto customerDto) {
        var currentUser = authenticationService.getCurrentUser();
        var currentCustomer = customerService.getCustomerByUser(currentUser);
        if (currentCustomer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var updatedCustomer = mergeCustomers(currentCustomer.get(), customerDto);
        updatedCustomer = customerService.updateCustomer(updatedCustomer);
        var res = customerMapper.mapToDto(updatedCustomer);
        res.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/analysis")
    public ResponseEntity<List<AnalysisDto>> getCustomerAnalysis() {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var analysis = analysisService.getAnalysysesByCustomer(customer.get());
        return ResponseEntity.ok(analysis.stream().map(analysisMapper::mapToDto).toList());
    }

    @PostMapping("/analysis")
    public ResponseEntity<List<AnalysisDto>> addCustomerAnalysis(@Valid @RequestBody List<AnalysisDto> analysisDtos) {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var analyzes = analysisDtos.stream()
                .map(analysisMapper::mapFromDto)
                .peek(item -> item.setCustomer(customer.get()))
                .toList();
        List<AnalysisDto> res = analysisService.addAnalysis(analyzes).stream().map(analysisMapper::mapToDto).toList();
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/analysis/{id}")
    public ResponseEntity<Void> deleteCustomerAnalysis(@PathVariable Long id) {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var analysis = analysisService.getAnalysisById(id);
        if (analysis.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (analysis.get().getCustomer().getId() != customer.get().getId()) {
            return ResponseEntity.status(403).build();
        }
        analysisService.deleteAnalysis(analysis.get());
        return ResponseEntity.ok().build();
    }

    public Customer mergeCustomers(Customer oldCustomer, CustomerDto newCustomer){
        if(newCustomer.firstName() != null){
            oldCustomer.setFirstName(newCustomer.firstName());
        }
        if(newCustomer.lastName() != null){
            oldCustomer.setLastName(newCustomer.lastName());
        }
        if(newCustomer.age() != null){
            oldCustomer.setAge(newCustomer.age());
        }
        if(newCustomer.weight() != null){
            oldCustomer.setWeight(newCustomer.weight());
        }
        if(newCustomer.height() != null){
            oldCustomer.setHeight(newCustomer.height());
        }
        if(newCustomer.gender() != null){
            oldCustomer.setGender(newCustomer.gender());
        }
        return oldCustomer;
    }
}
