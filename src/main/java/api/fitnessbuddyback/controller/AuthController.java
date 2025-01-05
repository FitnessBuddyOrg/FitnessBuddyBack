package api.fitnessbuddyback.controller;


import api.fitnessbuddyback.dto.LoginDTO;
import api.fitnessbuddyback.dto.RegisterDTO;
import api.fitnessbuddyback.dto.UserResponseDTO;
import api.fitnessbuddyback.entity.AuthProvider;
import api.fitnessbuddyback.entity.User;
import api.fitnessbuddyback.repository.UserRepository;
import api.fitnessbuddyback.security.CustomUserDetails;
import api.fitnessbuddyback.security.JwtUtil;
import api.fitnessbuddyback.service.AuthService;
import org.springframework.security.core.Authentication;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("auth/")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("login")
    public UserResponseDTO login(@RequestBody LoginDTO loginDTO) throws JOSEException {
        return authService.login(loginDTO);
    }

    @PostMapping("register")
    public UserResponseDTO register(@RequestBody RegisterDTO registerDTO) throws JOSEException {
        return authService.register(registerDTO);
    }

    @GetMapping("/oauth2/success")
    public UserResponseDTO oauth2Login(Authentication authentication) throws JOSEException {
        System.out.println("=== Debugging OAuth2 Login ===");
        System.out.println("Authentication Object: " + authentication);

        // Check if authentication is valid and its type
        if (authentication == null) {
            System.out.println("Authentication is null!");
            throw new IllegalStateException("OAuth2 authentication failed or invalid token.");
        }

        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            System.out.println("Authentication is NOT an instance of OAuth2AuthenticationToken.");
            System.out.println("Authentication Class: " + authentication.getClass().getName());
            throw new IllegalStateException("OAuth2 authentication failed or invalid token.");
        }

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        System.out.println("Token Details: " + token);
        System.out.println("Principal: " + token.getPrincipal());
        System.out.println("Attributes: " + token.getPrincipal().getAttributes());

        Map<String, Object> attributes = token.getPrincipal().getAttributes();

        String email = attributes.get("email").toString();
        String name = attributes.getOrDefault("name", "Unknown").toString();

        System.out.println("Extracted Email: " + email);
        System.out.println("Extracted Name: " + name);

        // Determine provider
        AuthProvider provider = AuthProvider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());
        System.out.println("OAuth Provider: " + provider);

        // Extract birthDate if available
        LocalDate birthDate;
        if (attributes.containsKey("birthdate")) {
            birthDate = LocalDate.parse(attributes.get("birthdate").toString());
        } else {
            birthDate = null;
        }
        System.out.println("Extracted Birthdate: " + birthDate);

        // Check if user exists or create a new user
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(""); // No password for OAuth users
            newUser.setAuthProvider(provider);
            newUser.setBirthDate(birthDate);
            newUser.setRole("USER");
            System.out.println("New User Created: " + newUser);
            return userRepository.save(newUser);
        });

        // Generate JWT token
        final CustomUserDetails userDetails = new CustomUserDetails(user.getEmail(), "",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())));
        final String accessToken = jwtUtil.generateToken(userDetails);

        System.out.println("Generated Access Token: " + accessToken);

        return new UserResponseDTO(accessToken);
    }




}
