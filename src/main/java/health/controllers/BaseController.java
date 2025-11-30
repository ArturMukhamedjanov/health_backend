package health.controllers;

import health.auth.services.AuthenticationService;
import health.models.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.function.Function;

/**
 * Base controller class providing common functionality for all controllers.
 * Reduces code duplication by extracting common patterns.
 */
@RequiredArgsConstructor
public abstract class BaseController {

    protected final AuthenticationService authenticationService;

    /**
     * Gets the current authenticated user.
     */
    protected User getCurrentUser() {
        return authenticationService.getCurrentUser();
    }

    /**
     * Executes an operation with the current user's entity.
     * Returns 404 if entity not found, otherwise executes the operation.
     *
     * @param entityFetcher Function to fetch entity by user
     * @param operation Function to execute with the entity
     * @return ResponseEntity with result or 404
     */
    protected <T, R> ResponseEntity<R> withUserEntity(
            Function<User, Optional<T>> entityFetcher,
            Function<T, ResponseEntity<R>> operation) {
        
        var currentUser = getCurrentUser();
        var entity = entityFetcher.apply(currentUser);
        
        if (entity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return operation.apply(entity.get());
    }

    /**
     * Executes an operation with an entity fetched by ID.
     * Returns 404 if entity not found, otherwise executes the operation.
     *
     * @param entityFetcher Function to fetch entity by ID
     * @param id Entity ID
     * @param operation Function to execute with the entity
     * @return ResponseEntity with result or 404
     */
    protected <T, R> ResponseEntity<R> withEntity(
            Function<Long, Optional<T>> entityFetcher,
            Long id,
            Function<T, ResponseEntity<R>> operation) {
        
        var entity = entityFetcher.apply(id);
        
        if (entity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return operation.apply(entity.get());
    }

    /**
     * Checks if an entity belongs to the owner.
     *
     * @param entityOwnerId ID of the entity owner
     * @param ownerId ID of the expected owner
     * @return true if entity belongs to owner
     */
    protected boolean belongsTo(Long entityOwnerId, Long ownerId) {
        return entityOwnerId.equals(ownerId);
    }

    /**
     * Returns 403 Forbidden response.
     */
    protected <T> ResponseEntity<T> forbidden() {
        return ResponseEntity.status(403).build();
    }

    /**
     * Returns 400 Bad Request response.
     */
    protected <T> ResponseEntity<T> badRequest() {
        return ResponseEntity.badRequest().build();
    }

    /**
     * Returns 404 Not Found response.
     */
    protected <T> ResponseEntity<T> notFound() {
        return ResponseEntity.notFound().build();
    }
}