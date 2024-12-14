package health.controllers;

import health.models.auth.Role;
import lombok.Builder;

@Builder(toBuilder = true)
public record LoginResponse(
        Role role
) {
}
