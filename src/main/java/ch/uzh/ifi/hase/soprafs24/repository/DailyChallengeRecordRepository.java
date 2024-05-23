package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.DailyChallengeRecord;
import ch.uzh.ifi.hase.soprafs24.entity.DailyChallengeRecordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("dailyChallengeRecordRepository")
public interface DailyChallengeRecordRepository extends JpaRepository<DailyChallengeRecord, DailyChallengeRecordId> {
    Optional<DailyChallengeRecord> findById(DailyChallengeRecordId id);
}
