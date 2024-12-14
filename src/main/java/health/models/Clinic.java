package health.models;

import health.models.auth.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "clinics")
@Getter
@Setter
@Builder(toBuilder = true)
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
