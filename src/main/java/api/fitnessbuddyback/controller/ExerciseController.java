package api.fitnessbuddyback.controller;

import api.fitnessbuddyback.dto.ShareExerciseDTO;
import api.fitnessbuddyback.dto.ShareExerciseResponseDTO;
import api.fitnessbuddyback.dto.TemplateExerciseDTO;
import api.fitnessbuddyback.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping("/share")
    public ShareExerciseResponseDTO shareExercise(@RequestBody ShareExerciseDTO shareExerciseDTO) {
        String token = exerciseService.shareExercise(shareExerciseDTO);
        return new ShareExerciseResponseDTO(token);
    }

    @GetMapping("/share/{token}")
    public ShareExerciseDTO getSharedExercise(@PathVariable String token) {
        return exerciseService.getExerciseByShareToken(token);
    }

    @GetMapping("/templates")
    public List<TemplateExerciseDTO> getTemplateExercises() {
        return exerciseService.getTemplateExercises();
    }

}
