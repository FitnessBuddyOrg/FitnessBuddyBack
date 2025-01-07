package api.fitnessbuddyback.controller;


import api.fitnessbuddyback.dto.*;
import api.fitnessbuddyback.entity.User;
import api.fitnessbuddyback.repository.UserRepository;
import api.fitnessbuddyback.security.CustomUserDetails;
import api.fitnessbuddyback.security.JwtUtil;
import api.fitnessbuddyback.service.AuthService;
import api.fitnessbuddyback.service.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import java.util.HashMap;
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

        User user = userService.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(null);
            user.setProvider("GOOGLE");
            user.setRole("USER");
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(new CustomUserDetails(email, "", List.of(new SimpleGrantedAuthority("ROLE_USER"))));
        return ResponseEntity.ok(new UserResponseDTO(token, user.getEmail()));
    }

    @GetMapping("/oauth2/authorization/github")
    public void redirectToGitHub(HttpServletResponse response) throws IOException {
        String clientId = "YOUR_GITHUB_CLIENT_ID"; // Use environment variable or config
        String redirectUri = "http://localhost:8080/auth/github/callback";
        String githubAuthUrl = "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=read:user,user:email";

        response.sendRedirect(githubAuthUrl);
    }

    @GetMapping("/github/callback")
    public ResponseEntity<UserResponseDTO> handleGitHubCallback(@RequestParam("code") String code) {
        try {
            // Exchange code for access token
            RestTemplate restTemplate = new RestTemplate();
            String tokenUri = "https://github.com/login/oauth/access_token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            Map<String, String> body = new HashMap<>();
            body.put("client_id", "YOUR_GITHUB_CLIENT_ID");
            body.put("client_secret", "YOUR_GITHUB_CLIENT_SECRET");
            body.put("code", code);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUri, request, Map.class);

            String accessToken = (String) tokenResponse.getBody().get("access_token");

            // Fetch user details from GitHub
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);

            HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);
            ResponseEntity<Map> userResponse = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    userRequest,
                    Map.class
            );

            String email = (String) userResponse.getBody().get("email");


            // Save or update user in database
            User user = userService.findByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setProvider("GITHUB");
                user.setRole("USER");
                userRepository.save(user);
            }

            // Generate JWT
            String token = jwtUtil.generateToken(new CustomUserDetails(email, "", List.of(new SimpleGrantedAuthority("ROLE_USER"))));
            return ResponseEntity.ok(new UserResponseDTO(token, user.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }







}
