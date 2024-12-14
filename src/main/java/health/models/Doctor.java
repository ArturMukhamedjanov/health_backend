package health.models;

import health.models.auth.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "_user")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "clinics")
    private Clinic clinic;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String speciality;

}
