package api.fitnessbuddyback.service;



import api.fitnessbuddyback.dto.AppOpenDTO;
import api.fitnessbuddyback.dto.UpdateUserDTO;
import api.fitnessbuddyback.dto.UserDTO;
import api.fitnessbuddyback.entity.AppOpen;
import api.fitnessbuddyback.entity.User;
import api.fitnessbuddyback.mapper.AppOpenMapper;
import api.fitnessbuddyback.repository.AppOpenRepository;
import api.fitnessbuddyback.repository.UserRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AppOpenRepository appOpenRepository;

    private final AppOpenMapper appOpenMapper;

    private final MinioClient minioClient;

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
        AppOpen appOpen = new AppOpen();
        appOpen.setUser(user);
        appOpen.setOpenTime(LocalDateTime.now());
        appOpenRepository.save(appOpen);
    }

    public List<AppOpenDTO> getAppOpenCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return appOpenMapper.toDTO(appOpenRepository.findByUserId(user.getId()));
    }

    public UserDTO patchUser(UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(updateUserDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setName(updateUserDTO.getName());
        return convertToDTO(userRepository.save(user));
    }

    public String uploadDefaultProfilePicture(Long userId) throws Exception {
        String defaultImagePath = "path/to/default/image.jpg";
        String objectName = "users/" + userId + "/profile-picture.jpg";

        try (InputStream inputStream = new FileInputStream(defaultImagePath)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("profile-pictures")
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType("image/jpeg")
                            .build()
            );
            return "https://minio-server/profile-pictures/" + objectName;
        }
    }

}
