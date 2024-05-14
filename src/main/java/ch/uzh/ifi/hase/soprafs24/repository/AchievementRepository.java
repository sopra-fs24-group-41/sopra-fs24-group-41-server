package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("achievementRepository")
public interface AchievementRepository extends JpaRepository<Achievement, String> {
    Achievement findByName(String name);
}
