package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@WebAppConfiguration
@SpringBootTest
class AchievementServiceIntegrationTest {

    @Qualifier("achievementRepository")
    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private AchievementService achievementService;

    @BeforeEach
    public void setup() {
        achievementRepository.deleteAll();
        achievementService.setup();
    }

    @Test
    void setup_success() {
        assertEquals(achievementService.getAchievements().size(), achievementRepository.findAll().size());
    }
}
