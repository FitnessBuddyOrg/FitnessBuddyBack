package api.fitnessbuddyback.repository;

import api.fitnessbuddyback.entity.AppOpen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppOpenRepository extends JpaRepository<AppOpen, Long> {
    List<AppOpen> findByUserId(Long userId);
}