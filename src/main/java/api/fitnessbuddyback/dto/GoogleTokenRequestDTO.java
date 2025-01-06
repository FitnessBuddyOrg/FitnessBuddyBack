package api.fitnessbuddyback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleTokenRequestDTO {
    private String idToken;
}
