package api.fitnessbuddyback.service;



import api.fitnessbuddyback.entity.User;
import api.fitnessbuddyback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String username) {
        return userRepository.findByEmail(username).orElseThrow();
    }
}
