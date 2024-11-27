package api.fitnessbuddyback.controller;


import api.fitnessbuddyback.dto.LoginDTO;
import api.fitnessbuddyback.dto.RegisterDTO;
import api.fitnessbuddyback.dto.UserResponseDTO;
import api.fitnessbuddyback.service.AuthService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("login")
    public UserResponseDTO login(@RequestBody LoginDTO loginDTO) throws JOSEException {
        return authService.login(loginDTO);
    }

    @PostMapping("register")
    public UserResponseDTO register(@RequestBody RegisterDTO registerDTO) throws JOSEException {
        return authService.register(registerDTO);
    }
}
