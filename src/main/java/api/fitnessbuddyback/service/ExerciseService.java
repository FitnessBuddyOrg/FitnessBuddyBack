package api.fitnessbuddyback.service;

import api.fitnessbuddyback.dto.ShareExerciseDTO;
import api.fitnessbuddyback.dto.TemplateExerciseDTO;
import api.fitnessbuddyback.entity.Exercise;
import api.fitnessbuddyback.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciceRepository;

    public String shareExercise(ShareExerciseDTO shareExerciseDTO) {
        Exercise exercise = new Exercise();
        exercise.setName(shareExerciseDTO.getName());
        exercise.setInstructions(shareExerciseDTO.getInstructions());
        exercise.setVideoLink(shareExerciseDTO.getVideoLink());
        exercise.setCategory(shareExerciseDTO.getCategory());
        exercise.setShareToken(createShareToken());
        return exerciceRepository.save(exercise).getShareToken();
    }

    public ShareExerciseDTO getExerciseByShareToken(String shareToken) {
        return exerciceRepository.findByShareToken(shareToken)
                .map(exercise -> ShareExerciseDTO.builder()
                        .name(exercise.getName())
                        .instructions(exercise.getInstructions())
                        .videoLink(exercise.getVideoLink())
                        .category(exercise.getCategory())
                        .build())
                .orElse(null);
    }

    public String createShareToken() {
        return UUID.randomUUID().toString();
    }

}
