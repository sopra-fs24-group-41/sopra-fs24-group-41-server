package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserRepository userRepository;

    private Player testPlayer1;

    private Player testPlayer2;

    private Lobby testLobby;

    private User testUser1;

    private User testUser2;

    @BeforeEach
    public void setup() {
        // given
        testLobby = new Lobby(1234, "test Lobby");
        testLobby.setMode(GameMode.STANDARD);

        testPlayer1 = new Player("123", "testplayer", null);
        testPlayer1.setPoints(32);
        // no value for AvailableWords set

        testPlayer2 = new Player("643", "anothertestplayer", null);
        testPlayer2.setPoints(54);

        testUser1 = new User();
        testUser1.setPassword("testPassword");
        testUser1.setUsername("firstname@lastname");
        testUser1.setStatus(UserStatus.OFFLINE);
        testUser1.setToken("1");

        testUser2 = new User();
        testUser2.setPassword("testPassword2");
        testUser2.setUsername("firstname@lastname2");
        testUser2.setStatus(UserStatus.OFFLINE);
        testUser2.setToken("2");

        testUser1.setPlayer(testPlayer1);
        testPlayer1.setUser(testUser1);

        testUser2.setPlayer(testPlayer2);
        testPlayer2.setUser(testUser2);

        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testPlayer2.setLobby(testLobby);
        testLobby.setPlayers(Arrays.asList(testPlayer1, testPlayer2));

        entityManager.persistAndFlush(testLobby);
    }

    @AfterEach
    public void cleanup() {
        lobbyRepository.deleteAll();
        playerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findById_success() {
        // when
        Player found = playerRepository.findById(testPlayer1.getId());

        // then
        assertEquals(testPlayer1.getName(), found.getName());
        assertEquals(testPlayer1.getToken(), found.getToken());
        assertEquals(testPlayer1.getPoints(), found.getPoints());
        assertEquals(testPlayer1.getUser(), found.getUser());
        assertEquals(testPlayer1.getAvailableWords(), found.getAvailableWords());
        assertEquals(testPlayer1.getOwnedLobby(), found.getOwnedLobby());
        assertEquals(testPlayer1.getLobby(), found.getLobby());
    }

    @Test
    public void findByToken_success() {
        // when
        Player found = playerRepository.findByToken(testPlayer1.getToken());

        // then
        assertEquals(testPlayer1.getName(), found.getName());
        assertEquals(testPlayer1.getToken(), found.getToken());
        assertEquals(testPlayer1.getPoints(), found.getPoints());
        assertEquals(testPlayer1.getUser(), found.getUser());
        assertEquals(testPlayer1.getAvailableWords(), found.getAvailableWords());
        assertEquals(testPlayer1.getOwnedLobby(), found.getOwnedLobby());
        assertEquals(testPlayer1.getLobby(), found.getLobby());
    }

    @Test
    public void findByUser_Id_success() {
        // when
        Player found = playerRepository.findByUser_Id(testPlayer1.getUser().getId());

        // then
        assertEquals(testPlayer1.getName(), found.getName());
        assertEquals(testPlayer1.getToken(), found.getToken());
        assertEquals(testPlayer1.getPoints(), found.getPoints());
        assertEquals(testPlayer1.getUser(), found.getUser());
        assertEquals(testPlayer1.getAvailableWords(), found.getAvailableWords());
        assertEquals(testPlayer1.getOwnedLobby(), found.getOwnedLobby());
        assertEquals(testPlayer1.getLobby(), found.getLobby());
    }

    @Test
    public void findByOwnedLobby_Code_success() {
        // when
        Player found = playerRepository.findByOwnedLobby_Code(testPlayer1.getOwnedLobby().getCode());

        // then
        assertEquals(testPlayer1.getName(), found.getName());
        assertEquals(testPlayer1.getToken(), found.getToken());
        assertEquals(testPlayer1.getPoints(), found.getPoints());
        assertEquals(testPlayer1.getUser(), found.getUser());
        assertEquals(testPlayer1.getAvailableWords(), found.getAvailableWords());
        assertEquals(testPlayer1.getOwnedLobby(), found.getOwnedLobby());
        assertEquals(testPlayer1.getLobby(), found.getLobby());
    }

    @Test
    public void findAllByLobby_Code_success() {
        // when
        List<Player> found = playerRepository.findAllByLobby_Code(testLobby.getCode());

        // then
        assertEquals(Arrays.asList(testPlayer1, testPlayer2), found);
    }
}
