package api.fitnessbuddyback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllLanguageTemplateExerciseDTO {
    private Long appId;
    private List<TemplateExerciseDTO> templateExerciseDTOList;

}
