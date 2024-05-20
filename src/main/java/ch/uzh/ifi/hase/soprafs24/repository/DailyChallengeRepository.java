package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.DailyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("dailyChallengeRepository")
public interface DailyChallengeRepository extends JpaRepository<DailyChallenge, Long> {
    DailyChallenge findById(long id);
}
