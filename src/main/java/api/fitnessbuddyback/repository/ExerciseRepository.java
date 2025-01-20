package api.fitnessbuddyback.repository;

import api.fitnessbuddyback.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByShareToken(String shareToken);

    Optional<List<Exercise>> findByTemplateIsTrue(boolean template);
}