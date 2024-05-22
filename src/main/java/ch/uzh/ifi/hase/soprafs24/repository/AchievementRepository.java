package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.achievements.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("achievementRepository")
public interface AchievementRepository extends JpaRepository<Achievement, String> {
    Optional<Achievement> findByName(String name);
}