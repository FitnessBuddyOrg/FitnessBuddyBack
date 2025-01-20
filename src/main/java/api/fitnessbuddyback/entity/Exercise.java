package api.fitnessbuddyback.entity;

import api.fitnessbuddyback.enumeration.Category;
import api.fitnessbuddyback.enumeration.Language;
import api.fitnessbuddyback.enumeration.ShareType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "exercise")
@Getter
@Setter
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long exerciseId;

    private Long appId;

    private String name;

    private String instructions;

    @Column(name = "video_link")
    private String videoLink = "https://www.youtube.com/watch?v=dGqI0Z5ul4k";

    @Enumerated(EnumType.STRING)
    private Category category = Category.ABS;

    @Column(name = "share_type")
    @Enumerated(EnumType.STRING)
    private ShareType shareType = ShareType.PRIVATE;

    @Enumerated(EnumType.STRING)
    private Language language = Language.CUSTOM;

    @Column(name = "user_id")
    private Long userId;

    private String shareToken;

    private boolean isTemplate = false;
}