package api.fitnessbuddyback.controller;

import api.fitnessbuddyback.dto.AppOpenDTO;
import api.fitnessbuddyback.dto.ProfilePictureDTO;
import api.fitnessbuddyback.dto.UpdateUserDTO;
import api.fitnessbuddyback.dto.UserDTO;
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

}