package health.models;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chats", indexes = {
    @Index(name = "idx_chat_clinic", columnList = "clinics"),
    @Index(name = "idx_chat_doctor", columnList = "doctors"),
    @Index(name = "idx_chat_customer", columnList = "customers"),
    @Index(name = "idx_chat_doctor_customer", columnList = "doctors, customers")
})
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
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

}