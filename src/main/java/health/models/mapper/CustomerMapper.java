package health.models.mapper;

import health.models.Customer;
import health.models.dto.CustomerDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerMapper {

    public Customer mapFromDto(CustomerDto customerDto) {
        var builder =  Customer.builder();
        customerDto.firstName().ifPresent(builder::firstName);
        customerDto.lastName().ifPresent(builder::lastName);
        customerDto.age().ifPresent(builder::age);
        customerDto.weight().ifPresent(builder::weight);
        customerDto.gender().ifPresent(builder::gender);
        customerDto.height().ifPresent(builder::height);
        return builder.build();
    }

    public CustomerDto mapToDto(Customer customer) {
        return CustomerDto.builder()
                .id(Optional.of(customer.getId()))
                .userId(Optional.ofNullable(customer.getUser().getId()))
                .firstName(Optional.ofNullable(customer.getFirstName()))
                .lastName(Optional.ofNullable(customer.getLastName()))
                .age(Optional.ofNullable(customer.getAge()))
                .weight(Optional.ofNullable(customer.getWeight()))
                .gender(Optional.ofNullable(customer.getGender()))
                .height(Optional.ofNullable(customer.getHeight()))
                .build();
    }

}
