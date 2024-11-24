package health.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import health.models.auth.User;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "_user")
    private User creator;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Min(value = 1, message = "Height must be greater than 0")
    @Column(nullable = false)
    private Integer age;

    @Min(value = 1, message = "Height must be greater than 0")
    @Column(nullable = false)
    private Integer weight; 

    @Column(nullable = false)
    private Gender gender;

    @Min(value = 1, message = "Height must be greater than 0")
    @Column(nullable = false)
    private Integer height;
    
    @Column(nullable = false, unique = true)
    private String email;   

}
