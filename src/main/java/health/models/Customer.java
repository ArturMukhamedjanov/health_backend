package health.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import health.models.auth.User;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;

import lombok.*;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "_user")
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Nullable
    @Min(value = 1, message = "Age must be greater than 0")
    private Integer age;

    @Nullable
    @Min(value = 1, message = "Age must be greater than 0")
    private Integer weight; 

    private Gender gender;

    @Nullable
    @Min(value = 1, message = "Age must be greater than 0")
    private Integer height;

}
