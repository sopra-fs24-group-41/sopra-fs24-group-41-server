package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

    @Qualifier("playerRepository")
    @Autowired
    PlayerRepository playerRepository;

    @Qualifier("userRepository")
    @Autowired
    UserRepository userRepository;

    @Qualifier("lobbyRepository")
    @Autowired
    LobbyRepository lobbyRepository;

    @Autowired
    PlayerService playerService;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
    }

    @Test
    public void checkToken_validInput_success() {
        Player testPlayer = new Player("234", "test", null);
        playerRepository.save(testPlayer);

        Player checkedPlayer = playerService.checkToken(testPlayer.getToken());
        assertEquals(testPlayer.getToken(), checkedPlayer.getToken());
        assertEquals(testPlayer.getPoints(), checkedPlayer.getPoints());
        assertEquals(testPlayer.getName(), checkedPlayer.getName());
    }

    @Test
    public void checkToken_invalidToken_throwsNotFoundException() {
        Player testPlayer = new Player("345", "tester", null);
        playerRepository.save(testPlayer);

        assertThrows(ResponseStatusException.class, () -> playerService.checkToken(testPlayer.getToken()+"23"));
    }

    @Test
    public void removePlayer_success() {
        assertNull(playerRepository.findByToken("678"));

        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
        testUser.setToken("678");
        testUser.setStatus(UserStatus.OFFLINE);
        User savedUser = userRepository.saveAndFlush(testUser);

        Lobby testLobby = new Lobby(123, "this is a new lobby");
        testLobby.setPublicAccess(true);

        Player player1 = new Player("123", "asdf", testLobby);
        player1.setOwnedLobby(testLobby);
        testLobby.setOwner(player1);
        testLobby.setPlayers(new ArrayList<>(Arrays.asList(player1)));

        Lobby savedLobby = lobbyRepository.saveAndFlush(testLobby);

        savedLobby.getOwner().setUser(savedUser);
        savedUser.setPlayer(savedLobby.getOwner());

        assertEquals(testLobby.getName(), savedLobby.getName());

        playerService.removePlayer(player1);

        assertNull(playerRepository.findByToken("678"));
    }
}
