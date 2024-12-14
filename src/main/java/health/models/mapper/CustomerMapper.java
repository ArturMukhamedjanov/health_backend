package health.models.mapper;

import health.models.Customer;
import health.models.dto.CustomerDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerMapper {

    public Customer mapFromDto(CustomerDto customerDto) {
        var builder =  Customer.builder();
        if(customerDto.firstName() != null){
            builder.firstName(customerDto.firstName());
        }
        if(customerDto.lastName() != null){
            builder.lastName(customerDto.lastName());
        }
        if(customerDto.age() != null){
            builder.age(customerDto.age());
        }
        if(customerDto.weight() != null){
            builder.weight(customerDto.weight());
        }
        if(customerDto.gender() != null){
            builder.gender(customerDto.gender());
        }
        if(customerDto.height() != null){
            builder.height(customerDto.height());
        }
        return builder.build();
    }

    public CustomerDto mapToDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .userId(customer.getUser().getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .age(customer.getAge())
                .weight(customer.getWeight())
                .gender(customer.getGender())
                .height(customer.getHeight())
                .build();
    }

}
