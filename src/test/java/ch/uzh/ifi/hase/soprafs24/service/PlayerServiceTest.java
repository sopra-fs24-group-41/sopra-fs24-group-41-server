package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private User testUser1;

    private Player testPlayer1;

    private Player testPlayer2;

    private Lobby testLobby;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testLobby = new Lobby(1234, "test Lobby");
        testLobby.setMode(GameMode.STANDARD);

        testPlayer1 = new Player("123", "testplayer", null);
        testPlayer1.setPoints(32);
        // no value for AvailableWords set

        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setPassword("testPassword");
        testUser1.setUsername("firstname@lastname");
        testUser1.setStatus(UserStatus.OFFLINE);
        testUser1.setToken("1");

        testPlayer2 = new Player("234", "testplayer2", null);
        testPlayer2.setPoints(12);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testPlayer2.setLobby(testLobby);
        testLobby.setPlayers(new ArrayList<>(Arrays.asList(testPlayer1, testPlayer2)));
    }

    @Test
    public void checkToken_validInput_success() {
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testPlayer1);

        Player checkedPlayer = playerService.checkToken(testPlayer1.getToken());
        assertEquals(testPlayer1.getId(), checkedPlayer.getId());
        assertEquals(testPlayer1.getName(), checkedPlayer.getName());
        assertEquals(testPlayer1.getPoints(), checkedPlayer.getPoints());
    }

    @Test
    public void checkToken_invalidToken_throwsNotFoundException() {
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> playerService.checkToken(Mockito.anyString()));
    }

    @Test
    public void removePlayer_success() {
        // given
        Mockito.doNothing().when(playerRepository).delete(Mockito.any());

        // when
        playerService.removePlayer(testPlayer1);

        // then
        Mockito.verify(playerRepository, Mockito.times(1)).delete(Mockito.any());
    }
}
