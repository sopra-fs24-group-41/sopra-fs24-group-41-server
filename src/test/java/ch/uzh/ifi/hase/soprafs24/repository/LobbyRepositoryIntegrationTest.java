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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LobbyRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    private Lobby testLobby;

    private Player testPlayer1;

    private Player testPlayer2;

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
        testUser1.setCreationDate(LocalDate.now());

        testUser2 = new User();
        testUser2.setPassword("testPassword2");
        testUser2.setUsername("firstname@lastname2");
        testUser2.setStatus(UserStatus.OFFLINE);
        testUser2.setToken("2");
        testUser2.setCreationDate(LocalDate.now());

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
    void cleanup() {
        lobbyRepository.deleteAll();
        playerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByCode_success() {
        // when
        Lobby found = lobbyRepository.findByCode(testLobby.getCode());

        // then
        assertEquals(testLobby.getCode(), found.getCode());
        assertEquals(testLobby.getName(), found.getName());
        assertEquals(testLobby.getPublicAccess(), found.getPublicAccess());
        assertEquals(testLobby.getStatus(), found.getStatus());
        assertEquals(testLobby.getMode(), found.getMode());
        assertEquals(testLobby.getOwner(), found.getOwner());
        assertEquals(testLobby.getPlayers(), found.getPlayers());
    }

    @Test
    void findAllByPublicAccess_success() {
        // given
        Lobby lobby2 = new Lobby(4521, "lobby2");
        lobby2.setPublicAccess(true);
        entityManager.persistAndFlush(lobby2);
        Lobby[] lobbyList = new Lobby[]{testLobby};

        // when
        List<Lobby> found = lobbyRepository.findAllByPublicAccess(testLobby.getPublicAccess());

        // then
        assertArrayEquals(lobbyList, found.toArray());
    }

    @Test
    void findByOwner_Id_success() {
        // when
        Lobby found = lobbyRepository.findByOwner_Id(testLobby.getOwner().getId());

        // then
        assertEquals(testLobby.getCode(), found.getCode());
        assertEquals(testLobby.getName(), found.getName());
        assertEquals(testLobby.getPublicAccess(), found.getPublicAccess());
        assertEquals(testLobby.getStatus(), found.getStatus());
        assertEquals(testLobby.getMode(), found.getMode());
        assertEquals(testLobby.getOwner(), found.getOwner());
        assertEquals(testLobby.getPlayers(), found.getPlayers());
    }

    @Test
    void findAllByMode_success() {
        // when
        List<Lobby> found = lobbyRepository.findAllByMode(testLobby.getMode());
        Lobby[] lobbyList = new Lobby[]{testLobby};

        // then
        assertArrayEquals(lobbyList, found.toArray());
    }

    @Test
    void findAllByStatus_success() {
        // when
        List<Lobby> found = lobbyRepository.findAllByStatus(testLobby.getStatus());
        Lobby[] lobbyList = new Lobby[]{testLobby};

        // then
        assertArrayEquals(lobbyList, found.toArray());
    }

    @Test
    void findByPlayersIsContaining_success() {
        // when
        Lobby found = lobbyRepository.findByPlayersIsContaining(testPlayer2);

        // then
        assertEquals(testLobby.getCode(), found.getCode());
        assertEquals(testLobby.getName(), found.getName());
        assertEquals(testLobby.getPublicAccess(), found.getPublicAccess());
        assertEquals(testLobby.getStatus(), found.getStatus());
        assertEquals(testLobby.getMode(), found.getMode());
        assertEquals(testLobby.getOwner(), found.getOwner());
        assertEquals(testLobby.getPlayers(), found.getPlayers());
    }

    @Test
    void existsByCode_success() {
        assertTrue(lobbyRepository.existsByCode(testLobby.getCode()));
    }

    @Test
    void cascadePlayers_success() {
        // when
        List<Player> found = playerRepository.findAllByLobby_Code(testLobby.getCode());

        // then
        assertEquals(Arrays.asList(testPlayer1, testPlayer2), found);
    }
}
