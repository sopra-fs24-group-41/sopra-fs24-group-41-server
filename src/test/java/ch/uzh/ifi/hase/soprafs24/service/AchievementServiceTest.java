package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.entity.achievements.Achievement;
import ch.uzh.ifi.hase.soprafs24.entity.achievements.CreatedMud;
import ch.uzh.ifi.hase.soprafs24.entity.achievements.CreatedZaddy;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AchievementServiceTest {
    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AchievementService achievementService;

    private Achievement achievement;
    private Lobby lobby;
    private Player player;
    private User user;
    private Combination combination;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        achievement = new CreatedMud();

        Mockito.when(achievementRepository.saveAndFlush(Mockito.any(Achievement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(achievementRepository.save(Mockito.any(Achievement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(achievementRepository.findById(achievement.getName()))
                .thenReturn(Optional.of(achievement));

        Mockito.doNothing().when(messagingTemplate).convertAndSend(Mockito.any());

        player = new Player("123", "player", null);
        user = new User();
        user.setId(1L);

        user.setPlayer(player);
        player.setUser(user);

        lobby = new Lobby(1234, "Lobby");
        lobby.setOwner(player);
        lobby.setStartTime(LocalDateTime.now());
        player.setOwnedLobby(lobby);

        player.setLobby(lobby);
        lobby.setPlayers(new ArrayList<>(Arrays.asList(player)));

        combination = new Combination(new Word("water"), new Word("earth"), new Word("mud"));

        achievementService.setup();
    }

    @Test
    void getAchievement_found() {
        Achievement foundAchievement = achievementService.get(achievement);

        assertEquals(achievement, foundAchievement);
    }

    @Test
    void getAchievement_new() {
        Achievement newAchievement = new CreatedZaddy();
        Achievement foundAchievement = achievementService.get(newAchievement);

        assertEquals(newAchievement, foundAchievement);
    }

    @Test
    void awardMudAchievement_success() {
        achievementService.awardAchievements(player, combination);

        assert(user.getAchievements().contains(achievement));
        assert(!user.getAchievements().contains((new CreatedZaddy())));
    }
}