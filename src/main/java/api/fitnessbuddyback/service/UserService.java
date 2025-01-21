package api.fitnessbuddyback.service;



import api.fitnessbuddyback.dto.AppOpenDTO;
import api.fitnessbuddyback.dto.RoutineDTO;
import api.fitnessbuddyback.dto.UpdateUserDTO;
import api.fitnessbuddyback.dto.UserDTO;
import api.fitnessbuddyback.entity.AppOpen;
import api.fitnessbuddyback.entity.Routine;
import api.fitnessbuddyback.entity.User;
import api.fitnessbuddyback.mapper.AppOpenMapper;
import api.fitnessbuddyback.mapper.RoutineMapper;
import api.fitnessbuddyback.repository.AppOpenRepository;
import api.fitnessbuddyback.repository.RoutineRepository;
import api.fitnessbuddyback.repository.UserRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AppOpenRepository appOpenRepository;

    private final RoutineRepository routineRepository;

    private final AppOpenMapper appOpenMapper;

    private final RoutineMapper routineMapper;

    private final MinioService minioService;

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



    public UserDTO patchUser(UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(updateUserDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setName(updateUserDTO.getName());
        return convertToDTO(userRepository.save(user));
    }

    public String getProfilePicture(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getProfilePictureUrl() != null) {
            if (user.getProfilePictureFromProvider()) {
                return user.getProfilePictureUrl();
            }
            else
            {
                return minioService.getProfilePictureUrl(user);
            }

        }
        return null;
    }

    public String updateProfilePicture(String username, MultipartFile file) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        System.out.println(file.getName());
        minioService.uploadProfilePicture(file, user);
        user.setProfilePictureFromProvider(false);
        user.setProfilePictureUrl("users/" + user.getId() + "/profile-picture.jpg");
        userRepository.save(user);
        return minioService.getProfilePictureUrl(user);
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
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return appOpenMapper.toDTO(appOpenRepository.findByUserId(user.getId())).stream()
                .filter(appOpenDTO -> appOpenDTO.getOpenTime().isAfter(oneWeekAgo))
                .toList();
    }

    public List<AppOpenDTO> getAllAppOpenCounts() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1).minusDays(1);
        return appOpenMapper.toDTO(appOpenRepository.findAll()).stream()
                .filter(appOpenDTO -> appOpenDTO.getOpenTime().isAfter(oneWeekAgo))
                .toList();
    }

    public void addCompletedRoutine(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Routine routine = new Routine();
        routine.setUser(user);
        routine.setCompletedTime(LocalDateTime.now());
        routineRepository.save(routine);
    }

    public List<RoutineDTO> getRoutineCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return routineMapper.toDTO(routineRepository.findByUserId(user.getId())).stream()
                .filter(routineDTO -> routineDTO.getCompletedTime().isAfter(oneWeekAgo))
                .toList();
    }

    public List<RoutineDTO> getAllRoutineCounts() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1).minusDays(1);
        return routineMapper.toDTO(routineRepository.findAll()).stream()
                .filter(routineDTO -> routineDTO.getCompletedTime().isAfter(oneWeekAgo))
                .toList();
    }


}
