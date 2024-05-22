package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
class GameServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("wordRepository")
    @Autowired
    private WordRepository wordRepository;

    @Qualifier("combinationRepository")
    @Autowired
    private CombinationRepository combinationRepository;

    @Qualifier("lobbyRepository")
    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private GameService gameService;

    @BeforeEach
    public void setup() {
        combinationRepository.deleteAll();
        wordRepository.deleteAll();
        userRepository.deleteAll();
        playerRepository.deleteAll();
        lobbyRepository.deleteAll();
    }

    @Test
    void play_updatesPlayerStatistics_success() {
        User user = new User();
        Player player = new Player();

        player.setUser(user);
        user.setPlayer(player);

        Lobby testLobby = new Lobby();
        testLobby.setMode(GameMode.WOMBOCOMBO);
        testLobby.setPlayers(List.of(player));
        testLobby.setStartTime(LocalDateTime.now());
        player.setLobby(testLobby);

        Word word1 = new Word("water", 0, 100.0);
        Word word2 = new Word("earth", 0, 100.0);

        gameService.play(player, List.of(word1, word2));

        assertEquals(1, user.getCombinationsMade());
        assertEquals(1, user.getDiscoveredWords());
        assertNotSame(word1, user.getRarestWordFound());
        assertNotSame(word2, user.getRarestWordFound());
    }
}
