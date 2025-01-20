package api.fitnessbuddyback.controller;

import api.fitnessbuddyback.dto.AllLanguageTemplateExerciseDTO;
import api.fitnessbuddyback.dto.ShareExerciseDTO;
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
    public String shareExercise(@RequestBody ShareExerciseDTO shareExerciseDTO) {
        return exerciseService.shareExercise(shareExerciseDTO);
    }

    @GetMapping("/share")
    public ShareExerciseDTO getSharedExercise(@RequestParam String token) {
        return exerciseService.getExerciseByShareToken(token);
    }

    @GetMapping("/templates")
    public List<TemplateExerciseDTO> getTemplateExercises() {
        return exerciseService.getTemplateExercises();
    }

}
