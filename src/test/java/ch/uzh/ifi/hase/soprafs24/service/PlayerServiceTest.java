package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlayerService playerService;

    private User testUser1;

    private User testUser2;

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
        // no value for AvailableWords set

//        testUser.setPlayer(testPlayer);
//        testPlayer.setUser(testUser);
//
//        testLobby.setOwner(testPlayer);
//        testPlayer.setOwnedLobby(testLobby);
//
//        testPlayer.setLobby(testLobby);
//        testLobby.setPlayers(new ArrayList<>(Arrays.asList(testPlayer)));
    }

    @Test
    public void checkToken_validInput_success() {
        // TODO: to be implemented
    }

    @Test
    public void checkToken_invalidToken_throwsNotFoundException() {
        // TODO: to be implemented
    }

    @Test
    public void removePlayer_success() {
        // TODO: to be implemented
    }

}
