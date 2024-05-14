package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AchievementServiceTest {
    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private AchievementService achievementService;

    private Achievement achievement;
    private Player player;
    private User user;
    private Combination combination;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        achievement = new Achievement(
                "This stuff is everywhere!",
                "Create mud through any combination",
                "MudPuddle.png");

        Mockito.when(achievementRepository.saveAndFlush(Mockito.any(Achievement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(achievementRepository.save(Mockito.any(Achievement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(achievementRepository.findByName(achievement.getName()))
                .thenReturn(achievement);

        player = new Player("123", "player", null);
        user = new User();
        user.setId(1L);

        user.setPlayer(player);
        player.setUser(user);

        combination = new Combination(new Word("water"), new Word("earth"), new Word("mud"));
    }

    @Test
    void getAchievement_foundAchievement() {
        Achievement foundAchievement = achievementService.getAchievement(achievement);

        assertEquals(achievement, foundAchievement);
    }

    @Test
    void getAchievement_newAchievement() {
        Achievement newAchievement = new Achievement("New", "New", "New.png");
        Achievement foundAchievement = achievementService.getAchievement(newAchievement);

        assertEquals(newAchievement, foundAchievement);
    }

    @Test
    void awardMudAchievement_success() {
        achievementService.awardAchievements(player, combination);

        assertEquals(1, user.getAchievements().size());
    }
}