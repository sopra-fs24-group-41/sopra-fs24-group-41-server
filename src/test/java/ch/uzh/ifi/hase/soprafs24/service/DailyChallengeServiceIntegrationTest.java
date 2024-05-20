package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.DailyChallengeRecordRepository;
import ch.uzh.ifi.hase.soprafs24.repository.DailyChallengeRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
class DailyChallengeServiceIntegrationTest {

    @Qualifier("dailyChallengeRepository")
    @Autowired
    private DailyChallengeRepository dailyChallengeRepository;

    @Qualifier("dailyChallengeRecordRepository")
    @Autowired
    private DailyChallengeRecordRepository dailyChallengeRecordRepository;

    @Autowired
    private WordService wordService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyChallengeService dailyChallengeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        dailyChallengeRecordRepository.deleteAll();
        dailyChallengeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createNewDailyChallenge_success() {
        dailyChallengeService.createNewDailyChallenge();

        assertFalse(dailyChallengeRepository.findAll().isEmpty());
    }

    @Test
    void updateRecords_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1234");
        user = userRepository.saveAndFlush(user);

        Word word = new Word("volcano", 3, 0.125);
        wordService.saveWord(word);

        Lobby lobby = new Lobby(123, "abcd");
        Player player = new Player("1234", "testUsername", lobby);
        player.setPoints(3L);
        player.setUser(user);
        user.setPlayer(player);
        lobby.setPlayers(List.of(player));

        dailyChallengeService.createNewDailyChallenge();
        dailyChallengeService.updateRecords(lobby);

        assertFalse(dailyChallengeRecordRepository.findAll().isEmpty());
        assertEquals(3L, dailyChallengeRecordRepository.findById(
                new DailyChallengeRecordId(dailyChallengeService.getDailyChallenge().getId(),
                        user.getId())).get().getNumberOfCombinations());
    }
}
