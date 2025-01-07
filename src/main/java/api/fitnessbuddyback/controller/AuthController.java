package api.fitnessbuddyback.controller;


import api.fitnessbuddyback.dto.GoogleTokenRequestDTO;
import api.fitnessbuddyback.dto.LoginDTO;
import api.fitnessbuddyback.dto.RegisterDTO;
import api.fitnessbuddyback.dto.UserResponseDTO;
import api.fitnessbuddyback.entity.User;
import api.fitnessbuddyback.repository.UserRepository;
import api.fitnessbuddyback.security.CustomUserDetails;
import api.fitnessbuddyback.security.JwtUtil;
import api.fitnessbuddyback.service.AuthService;
import api.fitnessbuddyback.service.UserService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("auth/")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public UserResponseDTO login(@RequestBody LoginDTO loginDTO) throws JOSEException {
        return authService.login(loginDTO);
    }

    @PostMapping("register")
    public UserResponseDTO register(@RequestBody RegisterDTO registerDTO) throws JOSEException {
        return authService.register(registerDTO);
    }

    @PostMapping("/login/oauth2/code/google")
    public ResponseEntity<UserResponseDTO> googleLogin(@RequestBody GoogleTokenRequestDTO idToken) throws JOSEException {
        String email = jwtUtil.verifyGoogleIdToken(idToken.getIdToken());
        System.out.println(email);
        User user = userService.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(null);
            user.setRole("USER");
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(new CustomUserDetails(email, "", List.of(new SimpleGrantedAuthority("ROLE_USER"))));
        return ResponseEntity.ok(new UserResponseDTO(token, user.getEmail()));
    }





}
