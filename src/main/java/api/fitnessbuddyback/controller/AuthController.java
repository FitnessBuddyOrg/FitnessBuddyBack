package api.fitnessbuddyback.controller;

import api.fitnessbuddyback.dto.GoogleTokenRequestDTO;
import api.fitnessbuddyback.dto.LoginDTO;
import api.fitnessbuddyback.dto.RegisterDTO;
import api.fitnessbuddyback.dto.UserResponseDTO;
import api.fitnessbuddyback.service.AuthService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;


@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

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
        return ResponseEntity.ok(authService.googleLogin(idToken));
    }

    @GetMapping("/login/oauth2/code/github")
    public void githubLogin(@RequestParam String code, HttpServletResponse response) throws JOSEException, IOException {
        String redirectUrl = authService.githubLogin(code);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login-failed")
    public ResponseEntity<String> loginFailed(@RequestParam(required = false) String error) {
        return ResponseEntity.ok("Login failed: " + error);
    }


}
