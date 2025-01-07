package api.fitnessbuddyback.service;


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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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


    public UserResponseDTO register(RegisterDTO registerDTO) throws JOSEException {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(registerDTO.getEmail());
        }
        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        final CustomUserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        final String accessToken = jwtUtil.generateToken(userDetails);
        return new UserResponseDTO(accessToken, user.getEmail());
    }
    public UserResponseDTO login(LoginDTO loginDTO) throws JOSEException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid email/password");
        }

        final CustomUserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
        final String accessToken = jwtUtil.generateToken(userDetails);
        return new UserResponseDTO(accessToken, loginDTO.getEmail());
    }

}
