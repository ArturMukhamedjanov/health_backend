package health.utils;

import health.models.Chat;
import health.models.Message;
import health.models.auth.Role;

import java.time.Instant;

/**
 * Utility class for creating chat messages.
 * Centralizes message creation logic to avoid duplication.
 */
public class ChatMessageUtil {

    private ChatMessageUtil() {
        // Utility class, prevent instantiation
    }

    /**
     * Creates a new message for a chat.
     *
     * @param chat The chat to add the message to
     * @param text The message text
     * @param role The role of the sender (CUSTOMER or DOCTOR)
     * @return New message entity
     */
    public static Message createMessage(Chat chat, String text, Role role) {
        return Message.builder()
                .chat(chat)
                .role(role)
                .text(text)
                .sendTime(Instant.now())
                .build();
    }
}