package health.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import health.models.auth.User;

public interface UserRepo extends JpaRepository<User, Long>{

    Optional<User> findByUsername(String username);

    public User getUserByUsername(String username);
    
}
