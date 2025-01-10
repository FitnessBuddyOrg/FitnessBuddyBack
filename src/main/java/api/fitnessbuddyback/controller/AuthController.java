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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String secretId;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;



    @PostMapping("/auth/login")
    public UserResponseDTO login(@RequestBody LoginDTO loginDTO) throws JOSEException {
        return authService.login(loginDTO);
    }

    @PostMapping("/auth/register")
    public UserResponseDTO register(@RequestBody RegisterDTO registerDTO) throws JOSEException {
        return authService.register(registerDTO);
    }

    @PostMapping("/auth/login/oauth2/code/google")
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

    @GetMapping("/login/oauth2/code/github")
    public void githubLogin(@RequestParam String code, HttpServletResponse response) throws JOSEException, IOException {
        String accessToken = getAccessTokenFromGitHub(code);
        String email = getEmailFromGitHub(accessToken);

        User user = userService.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setProvider("GITHUB");
            user.setRole("USER");
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(new CustomUserDetails(email, "", List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        String redirectUrl = "fitnessbuddy://auth?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) +
                "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);

    }



    private String getAccessTokenFromGitHub(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept", "application/json");
        headers.add("Accept-Encoding", "application/json");

        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", secretId);
        body.put("code", code);
        body.put("redirect_uri", redirectUri);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to exchange code for access token.", e);
        }

        if (response.getBody() != null && response.getBody().containsKey("access_token")) {
            String accessToken = (String) response.getBody().get("access_token");
            return accessToken;
        } else {
            throw new RuntimeException("Failed to retrieve access token from GitHub: " + response.getBody());
        }
    }



    private String getEmailFromGitHub(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUrl = "https://api.github.com/user/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, request,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        if (response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> primaryEmail = response.getBody().stream()
                    .filter(email -> Boolean.TRUE.equals(email.get("primary")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No primary email found"));
            return (String) primaryEmail.get("email");
        } else {
            throw new RuntimeException("Failed to retrieve email from GitHub");
        }
    }

    @GetMapping("/login-failed")
    public ResponseEntity<String> loginFailed(@RequestParam(required = false) String error) {
        return ResponseEntity.ok("Login failed: " + error);
    }


}
