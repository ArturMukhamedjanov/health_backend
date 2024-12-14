package health.models;

import health.models.auth.User;
import lombok.*;

import javax.persistence.*;
import javax.print.Doc;
import java.time.Instant;

@Entity
@Table(name = "timetable")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctors")
    private Doctor doctor;

    @Column(nullable = false, unique = true)
    private Instant start;

    @Column(nullable = false)
    private boolean reserved;
}
