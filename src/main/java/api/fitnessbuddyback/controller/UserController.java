package api.fitnessbuddyback.controller;

import api.fitnessbuddyback.entity.User;
import api.fitnessbuddyback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/all")
    public Page<User> getAllUsers(Pageable pageable) {
        return userService.findAllUsers(pageable);
    }

    @GetMapping("/one")
    public User getOneUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }
}
