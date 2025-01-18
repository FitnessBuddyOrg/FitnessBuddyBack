package api.fitnessbuddyback.service;



import api.fitnessbuddyback.dto.UserDTO;
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

    public UserDTO findByEmail(String email) {
        return convertToDTO(userRepository.findByEmail(email).orElse(null));
    }

    public Page<UserDTO> findAllUsers(Pageable pageable) {
        return convertToDTO(userRepository.findAll(pageable));
    }

    private UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getProvider());
    }

    private Page<UserDTO> convertToDTO(Page<User> users) {
        return users.map(this::convertToDTO);
    }

    public void incrementAppOpenCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAppOpenCount(user.getAppOpenCount() + 1);
        userRepository.save(user);
    }

    public int getAppOpenCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getAppOpenCount();
    }

}
