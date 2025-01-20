package api.fitnessbuddyback.service;

import api.fitnessbuddyback.dto.AllLanguageTemplateExerciseDTO;
import api.fitnessbuddyback.dto.ShareExerciseDTO;
import api.fitnessbuddyback.dto.TemplateExerciseDTO;
import api.fitnessbuddyback.entity.Exercise;
import api.fitnessbuddyback.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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


    public List<TemplateExerciseDTO> getTemplateExercises() {
        List<Exercise> templateExercises = exerciceRepository.findByIsTemplateTrue().orElse(Collections.emptyList());

        return templateExercises.stream()
                .map(exercise -> TemplateExerciseDTO.builder()
                        .name(exercise.getName())
                        .instructions(exercise.getInstructions())
                        .videoLink(exercise.getVideoLink())
                        .category(exercise.getCategory())
                        .language(exercise.getLanguage())
                        .build())
                .collect(Collectors.toList());
    }
}
