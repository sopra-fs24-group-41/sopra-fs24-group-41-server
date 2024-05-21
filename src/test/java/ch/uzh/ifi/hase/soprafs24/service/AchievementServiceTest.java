package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.entity.achievements.*;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.InstructionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AchievementServiceTest {
    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AchievementService achievementService;

    private Player player;
    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(achievementRepository.saveAndFlush(Mockito.any(Achievement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(achievementRepository.save(Mockito.any(Achievement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.doNothing().when(messagingTemplate).convertAndSend(Mockito.anyString(), Mockito.any(InstructionDTO.class));

        player = new Player("a", "Player", null);
        user = new User();
        user.setId(1L);

        user.setPlayer(player);
        player.setUser(user);

        Lobby lobby = new Lobby(1, "Player's lobby");
        lobby.setOwner(player);
        lobby.setStartTime(LocalDateTime.now());
        player.setOwnedLobby(lobby);

        player.setLobby(lobby);
        lobby.setPlayers(Collections.singletonList(player));

        achievementService.setup();
    }

    @Test
    void getAchievement_found() {
        Achievement achievement = new CreatedMud();
        Mockito.when(achievementRepository.findById(achievement.getName()))
                .thenReturn(Optional.of(achievement));

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
    void awardMultipleAchievements_success() {
        Combination combination = new Combination(new Word("water"), new Word("earth"), new Word("mud"));

        achievementService.awardAchievements(player, combination);

        assert(user.getAchievements().contains(new CreatedMud()));
        assert(user.getAchievements().contains(new MadeFirstCombination()));
        assert(!user.getAchievements().contains((new CreatedZaddy())));
        Mockito.verify(messagingTemplate, Mockito.times(2)).convertAndSend(Mockito.anyString(), Mockito.any(InstructionDTO.class));
    }
}