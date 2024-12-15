package health.models;


import health.models.auth.User;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "analysis")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customers")
    private Customer customer;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private Instant date;

}
