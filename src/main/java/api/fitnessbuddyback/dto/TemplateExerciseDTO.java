package api.fitnessbuddyback.dto;

import api.fitnessbuddyback.enumeration.Category;
import api.fitnessbuddyback.enumeration.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateExerciseDTO {
    private String name;

    private String instructions;

    private String videoLink;

    private Category category;

    private Language language;

}
