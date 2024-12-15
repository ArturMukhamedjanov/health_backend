package health.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "clinics")
    private Clinic clinic;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctors")
    private Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customers")
    private Customer customer;

    @OneToOne(optional = false)
    @JoinColumn(name = "timetables")
    private Timetable timetable;
}
