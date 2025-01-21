package api.fitnessbuddyback.controller;

import api.fitnessbuddyback.dto.*;
import api.fitnessbuddyback.entity.AppOpen;
import api.fitnessbuddyback.security.CustomUserDetails;
import api.fitnessbuddyback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/all")
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userService.findAllUsers(pageable);
    }

    @GetMapping("/one")
    public UserDTO getOneUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/me")
    public UserDTO getMe() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        return userService.findByEmail(email);
    }

    @PostMapping("/increment-app-open")
    public void incrementAppOpenCount() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        userService.incrementAppOpenCount(userId);
    }

    @GetMapping("/app-open-count/{userId}")
    public ResponseEntity<List<AppOpenDTO>> getAppOpenCount(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getAppOpenCount(userId));
    }

    @GetMapping("/app-open-count/all")
    public ResponseEntity<List<AppOpenDTO>> getAllAppOpenCounts() {
        return ResponseEntity.ok(userService.getAllAppOpenCounts());
    }

    @PatchMapping("/patch")
    public UserDTO patchUser(@RequestBody UpdateUserDTO updateUserDTO) {
        return userService.patchUser(updateUserDTO);
    }

    @GetMapping("/profile-picture")
    public ProfilePictureDTO getProfilePicture() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ProfilePictureDTO(userService.getProfilePicture(userDetails.getUsername()));
    }

    @PatchMapping("/profile-picture")
    public ProfilePictureDTO updateProfilePicture(@RequestParam("file") MultipartFile file) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ProfilePictureDTO(userService.updateProfilePicture(userDetails.getUsername(), file));
    }

    @PostMapping("/add-completed-routine")
    public void addCompletedRoutine() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        userService.addCompletedRoutine(userId);
    }

    @GetMapping("/completed-routines")
    public ResponseEntity<List<RoutineDTO>> getCompletedRoutines() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(userService.getRoutineCount(userId));
    }

    @GetMapping("/completed-routines/all")
    public ResponseEntity<List<RoutineDTO>> getAllCompletedRoutines() {
        return ResponseEntity.ok(userService.getAllRoutineCounts());
    }

}