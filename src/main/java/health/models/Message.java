package health.models;

import health.models.auth.Role;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_message_chat", columnList = "chats"),
    @Index(name = "idx_message_sendtime", columnList = "sendTime"),
    @Index(name = "idx_message_chat_sendtime", columnList = "chats, sendTime")
})
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chats")
    private Chat chat;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Instant sendTime;
}
