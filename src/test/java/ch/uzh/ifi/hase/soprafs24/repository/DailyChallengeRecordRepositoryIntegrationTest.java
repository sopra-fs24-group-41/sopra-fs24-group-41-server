package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class DailyChallengeRecordRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private DailyChallengeRepository dailyChallengeRepository;

    @Autowired
    private DailyChallengeRecordRepository dailyChallengeRecordRepository;

    private User user;

    private DailyChallenge dailyChallenge;

    private DailyChallengeRecord dailyChallengeRecord;

    @BeforeEach
    void setup() {
        dailyChallengeRecordRepository.deleteAll();
        dailyChallengeRepository.deleteAll();
        wordRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1234");
        user = entityManager.merge(user);
        entityManager.persistAndFlush(user);

        Word word = new Word("steam");
        word = entityManager.merge(word);
        entityManager.persistAndFlush(word);

        dailyChallenge = new DailyChallenge(1L, word);
        dailyChallenge = entityManager.merge(dailyChallenge);
        entityManager.persistAndFlush(dailyChallenge);

        dailyChallengeRecord = new DailyChallengeRecord(dailyChallenge, user, 3);
        entityManager.persistAndFlush(dailyChallengeRecord);
    }

    @Test
    void findsById_success() {
        Optional<DailyChallengeRecord> foundRecord = dailyChallengeRecordRepository.findById(
                new DailyChallengeRecordId(dailyChallenge.getId(), user.getId())
        );

        assertFalse(foundRecord.isEmpty());
        assertEquals(dailyChallengeRecord, foundRecord.get());
    }

    @Test
    void findsByIdAndUpdates_success() {
        Optional<DailyChallengeRecord> foundRecord = dailyChallengeRecordRepository.findById(
                new DailyChallengeRecordId(dailyChallenge.getId(), user.getId())
        );

        assertFalse(foundRecord.isEmpty());

        foundRecord.get().setNumberOfCombinations(4);

        assertEquals(4, foundRecord.get().getNumberOfCombinations());
    }
}
