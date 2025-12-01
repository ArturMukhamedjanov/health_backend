package health.models;

import health.models.auth.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "clinics", indexes = {
    @Index(name = "idx_clinic_user", columnList = "_user"),
    @Index(name = "idx_clinic_name", columnList = "name")
})
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "_user")
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
}
