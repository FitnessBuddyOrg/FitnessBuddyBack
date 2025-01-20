package api.fitnessbuddyback.dto;

import api.fitnessbuddyback.enumeration.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareExerciseDTO {
    private String name;
    private String instructions;
    private String videoLink;
    private Category category;

}
