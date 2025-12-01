package health.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "appointments", indexes = {
    @Index(name = "idx_appointment_clinic", columnList = "clinics"),
    @Index(name = "idx_appointment_doctor", columnList = "doctors"),
    @Index(name = "idx_appointment_customer", columnList = "customers"),
    @Index(name = "idx_appointment_timetable", columnList = "timetables"),
    @Index(name = "idx_appointment_doctor_customer", columnList = "doctors, customers")
})
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
