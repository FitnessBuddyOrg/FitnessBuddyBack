package api.fitnessbuddyback.controller;

import api.fitnessbuddyback.dto.AppOpenDTO;
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

import java.util.List;


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



}