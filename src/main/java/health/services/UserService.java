package health.services;

import health.models.auth.User;
import health.repos.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepository;

    public User registerUser(User user){
       return userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }
}
