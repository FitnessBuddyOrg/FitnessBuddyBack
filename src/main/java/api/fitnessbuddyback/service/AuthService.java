package api.fitnessbuddyback.service;


import api.fitnessbuddyback.dto.GoogleTokenRequestDTO;
import api.fitnessbuddyback.dto.LoginDTO;
import api.fitnessbuddyback.dto.RegisterDTO;
import api.fitnessbuddyback.dto.UserResponseDTO;
import api.fitnessbuddyback.entity.User;
import api.fitnessbuddyback.exeption.InvalidCredentialsException;
import api.fitnessbuddyback.exeption.PasswordMismatchException;
import api.fitnessbuddyback.exeption.UserAlreadyExistsException;
import api.fitnessbuddyback.repository.UserRepository;
import api.fitnessbuddyback.security.CustomUserDetails;
import api.fitnessbuddyback.security.CustomUserDetailsService;
import api.fitnessbuddyback.security.JwtUtil;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String secretId;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;


    public UserResponseDTO register(RegisterDTO registerDTO) throws JOSEException {

        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(registerDTO.getEmail());
        }

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setProvider("LOCAL");
        user.setRole("USER");
        user.setProfilePictureFromProvider(false);
        userRepository.save(user);

        final CustomUserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        final String accessToken = jwtUtil.generateToken(userDetails);
        return new UserResponseDTO(accessToken, user.getEmail(), user.getId());
    }
    public UserResponseDTO login(LoginDTO loginDTO) throws JOSEException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid email/password");
        }

        final CustomUserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
        final String accessToken = jwtUtil.generateToken(userDetails);
        Long userId = userRepository.findByEmail(loginDTO.getEmail()).get().getId();
        return new UserResponseDTO(accessToken, loginDTO.getEmail(), userId);
    }

    public UserResponseDTO googleLogin(GoogleTokenRequestDTO idToken) throws JOSEException {
        String email = jwtUtil.verifyGoogleIdToken(idToken.getIdToken());
        String profilePictureUrl = idToken.getProfilePictureUrl();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(null);
            user.setProvider("GOOGLE");
            user.setRole("USER");

            if (profilePictureUrl != null){
                user.setProfilePictureUrl(profilePictureUrl);
                user.setProfilePictureFromProvider(true);
            }
            else {
                user.setProfilePictureFromProvider(false);
            }
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(new CustomUserDetails(email, "", List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole())), user.getProvider()));
        return new UserResponseDTO(token, user.getEmail(), user.getId());
    }

    public String githubLogin(String code) throws JOSEException {
        String accessToken = getAccessTokenFromGitHub(code);
        String email = getEmailFromGitHub(accessToken);
        String profilePictureUrl = getProfilePictureFromGitHub(accessToken);



        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {

            user = new User();
            user.setEmail(email);
            user.setProvider("GITHUB");
            user.setRole("USER");
            if (profilePictureUrl != null){
                user.setProfilePictureUrl(profilePictureUrl);
                user.setProfilePictureFromProvider(true);
            }
            else {
                user.setProfilePictureFromProvider(false);
            }

            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(new CustomUserDetails(email, "", List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole())), user.getProvider()));
        return "fitnessbuddy://auth?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) +
                "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)+ "&id=" + URLEncoder.encode(user.getId().toString(), StandardCharsets.UTF_8);
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


    private String getProfilePictureFromGitHub(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUrl = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, request, Map.class
        );

        return (String) response.getBody().get("avatar_url");
    }


}
