package api.fitnessbuddyback.dto;

import api.fitnessbuddyback.enumeration.Category;
import api.fitnessbuddyback.enumeration.Language;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(name = "video_link")
    private String videoLink = "https://www.youtube.com/watch?v=dGqI0Z5ul4k";

    @Enumerated(EnumType.STRING)
    private Category category = Category.ABS;

    @Enumerated(EnumType.STRING)
    private Language language = Language.CUSTOM;

}
