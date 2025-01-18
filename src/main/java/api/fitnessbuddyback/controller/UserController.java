package api.fitnessbuddyback.controller;

import api.fitnessbuddyback.dto.UserDTO;
import api.fitnessbuddyback.security.CustomUserDetails;
import api.fitnessbuddyback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("/{userId}/app-open-count")
    public ResponseEntity<Integer> getAppOpenCount(@PathVariable Long userId) {
        int count = userService.getAppOpenCount(userId);
        return ResponseEntity.ok(count);
    }

}