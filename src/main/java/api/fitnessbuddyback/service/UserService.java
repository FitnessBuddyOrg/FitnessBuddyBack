package api.fitnessbuddyback.service;



import api.fitnessbuddyback.entity.User;
import api.fitnessbuddyback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
