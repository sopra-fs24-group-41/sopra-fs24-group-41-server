package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@SpringBootTest
class LobbyServiceIntegrationTest {

    @Qualifier("lobbyRepository")
    @Autowired
    private LobbyRepository lobbyRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    public void setup() {
        lobbyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createLobbyByUser_validInputs_success() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        testUser.setCreationDate(LocalDate.now());

        User savedTestUser = userRepository.saveAndFlush(testUser);

        // when
        Player createdPlayer = lobbyService.createLobbyFromUser(savedTestUser, true);

        //then
        assertNotNull(createdPlayer.getName());
        assertNotNull(createdPlayer.getLobby().getName());
        assertEquals(true, createdPlayer.getLobby().getPublicAccess());
        assertEquals(LobbyStatus.PREGAME, createdPlayer.getLobby().getStatus());
        assertEquals(savedTestUser, createdPlayer.getLobby().getOwner().getUser());
        assertEquals(savedTestUser, createdPlayer.getUser());
        assertEquals(savedTestUser.getPlayer(), createdPlayer);
    }

    @Test
    void createLobbyByUser_nullPublicAccess_defaultToTrue() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        testUser.setCreationDate(LocalDate.now());

        User savedTestUser = userRepository.saveAndFlush(testUser);

        // when
        Player createdPlayer = lobbyService.createLobbyFromUser(savedTestUser, null);

        // then
        assertEquals(true, createdPlayer.getLobby().getPublicAccess());
    }

    @Test
    @Transactional
    void getLobbyByCode_validCode_returnsLobby() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        Lobby testLobby = new Lobby(1234, "this is a new lobby");
        testLobby.setPublicAccess(true);

        Player player1 = new Player("123", "asdf", testLobby);
        Player player2 = new Player("234", "jklö", testLobby);
        player1.setOwnedLobby(testLobby);
        testLobby.setOwner(player1);

        testLobby.setPlayers(new ArrayList<>(Arrays.asList(player1, player2)));

        Lobby savedLobby = lobbyRepository.saveAndFlush(testLobby);

        assertEquals(testLobby.getName(), savedLobby.getName());

        // when
        Lobby foundLobby = lobbyService.getLobbyByCode(1234);

        // then
        assertEquals(savedLobby, foundLobby);
    }

    @Test
    void getLobbyByCode_invalidCode_throwsNotFoundException() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        // when then
        assertThrows(ResponseStatusException.class, () -> lobbyService.getLobbyByCode(1234));
    }

    @Test
    void joinLobbyByUser_validInputs_success() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        testUser.setCreationDate(LocalDate.now());

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setPassword("testPassword");
        testUser2.setUsername("firstname2@lastname2");
        testUser2.setStatus(UserStatus.OFFLINE);
        testUser2.setToken("2");
        testUser2.setCreationDate(LocalDate.now());

        User savedTestUser = userRepository.saveAndFlush(testUser);
        User savedTestUser2 = userRepository.saveAndFlush(testUser2);
        Player testPlayer = lobbyService.createLobbyFromUser(savedTestUser, true);

        // when
        Player joinedPlayer = lobbyService.joinLobbyFromUser(savedTestUser2, testPlayer.getLobby().getCode());

        //then
        assertNotNull(testPlayer.getName(), joinedPlayer.getName());
        assertNotNull(joinedPlayer.getLobby().getName());
        assertEquals(true, joinedPlayer.getLobby().getPublicAccess());
        assertNotNull(joinedPlayer.getLobby().getStatus());
        assertEquals(testPlayer.getLobby().getCode(), joinedPlayer.getLobby().getCode());
    }

    @Test
    void joinLobbyByUser_invalidCode_throwsNotFoundException() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");

        // when then
        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyFromUser(testUser, 234));
    }

    @Test
    void joinLobbyByUser_invalidStatus_throwsForbiddenException() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        testUser.setCreationDate(LocalDate.now());

        User savedTestUser = userRepository.saveAndFlush(testUser);
        Player testPlayer = lobbyService.createLobbyFromUser(savedTestUser, true);
        testPlayer.getLobby().setStatus(LobbyStatus.INGAME);
        long lobbyCode = testPlayer.getLobby().getCode();
        lobbyRepository.saveAndFlush(testPlayer.getLobby());

        // when then
        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyFromUser(savedTestUser, lobbyCode));
    }

    @Test
    void joinLobbyAnonymous_validInputs_success() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        testUser.setCreationDate(LocalDate.now());

        User savedTestUser = userRepository.saveAndFlush(testUser);
        Player testPlayer = lobbyService.createLobbyFromUser(savedTestUser, true);

        // when
        Player joinedPlayer = lobbyService.joinLobbyAnonymous("anonymous", testPlayer.getLobby().getCode());

        //then
        assertNotNull(testPlayer.getName(), joinedPlayer.getName());
        assertNotNull(joinedPlayer.getLobby().getName());
        assertEquals(true, joinedPlayer.getLobby().getPublicAccess());
        assertNotNull(joinedPlayer.getLobby().getStatus());
        assertEquals(testPlayer.getLobby().getCode(), joinedPlayer.getLobby().getCode());
    }

    @Test
    void joinLobbyAnonymous_invalidCode_throwsNotFoundException() {
        // given
        assertNull(lobbyRepository.findByCode(234));

        // when then
        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyAnonymous("anonymous", 234));
    }

    @Test
    void joinLobbyAnonymous_invalidStatus_throwsForbiddenException() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        testUser.setCreationDate(LocalDate.now());

        User savedTestUser = userRepository.saveAndFlush(testUser);
        Player testPlayer = lobbyService.createLobbyFromUser(savedTestUser, true);
        testPlayer.getLobby().setStatus(LobbyStatus.INGAME);
        long lobbyCode = testPlayer.getLobby().getCode();
        lobbyRepository.saveAndFlush(testPlayer.getLobby());

        // when then
        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyAnonymous("anonymous", lobbyCode));
    }

    @Test
    @Transactional
    void joinLobbyByUser_ownerRejoinsLobby_success() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");
        testUser.setCreationDate(LocalDate.now());

        User savedTestUser = userRepository.saveAndFlush(testUser);
        Player testPlayer = lobbyService.createLobbyFromUser(savedTestUser, true);
        String oldToken = testPlayer.getToken();

        // when
        Player joinedPlayer = lobbyService.joinLobbyFromUser(savedTestUser, testPlayer.getLobby().getCode());

        //then
        assertNotNull(testPlayer.getName(), joinedPlayer.getName());
        assertNotNull(joinedPlayer.getLobby().getName());
        assertEquals(true, joinedPlayer.getLobby().getPublicAccess());
        assertNotNull(joinedPlayer.getLobby().getStatus());
        assertEquals(testPlayer.getLobby().getCode(), joinedPlayer.getLobby().getCode());
        assertNotEquals(oldToken, joinedPlayer.getToken());
    }

    @Test
    @Transactional
    void getPublicLobbies_returnsPublicLobbies() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        Lobby testLobby = new Lobby(1234, "this is a new lobby");
        testLobby.setPublicAccess(true);

        Player player1 = new Player("123", "asdf", testLobby);
        Player player2 = new Player("234", "jklö", testLobby);
        player1.setOwnedLobby(testLobby);
        testLobby.setOwner(player1);

        testLobby.setPlayers(new ArrayList<>(Arrays.asList(player1, player2)));

        Lobby savedLobby = lobbyRepository.saveAndFlush(testLobby);

        assertEquals(testLobby.getName(), savedLobby.getName());

        // when
        lobbyService.getPublicLobbies();

        // then
        assertEquals(1, lobbyService.getPublicLobbies().size());
        assertEquals(savedLobby, lobbyService.getPublicLobbies().get(0));
    }

    @Test
    @Transactional
    void updateLobby_validInputs_success() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        Lobby testLobby = new Lobby(1234, "this is a new lobby");
        testLobby.setPublicAccess(true);

        Player player1 = new Player("123", "asdf", testLobby);
        Player player2 = new Player("234", "jklö", testLobby);
        player1.setOwnedLobby(testLobby);
        testLobby.setOwner(player1);

        testLobby.setPlayers(new ArrayList<>(Arrays.asList(player1, player2)));

        Lobby savedLobby = lobbyRepository.saveAndFlush(testLobby);

        assertEquals(testLobby.getName(), savedLobby.getName());

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setPublicAccess(false);
        lobbyPutDTO.setMode(GameMode.FUSIONFRENZY);
        lobbyPutDTO.setName("new name");

        // when
        Lobby lobby = lobbyService.updateLobby(savedLobby, lobbyPutDTO);
        Map<String, Boolean> updates = lobby.getUpdatedFields();

        assertEquals(true, updates.get("publicAccess"));
        assertEquals(true, updates.get("mode"));
        assertEquals(true, updates.get("name"));
        assertEquals(lobbyPutDTO.getPublicAccess(), savedLobby.getPublicAccess());
        assertEquals(lobbyPutDTO.getMode(), savedLobby.getMode());
        assertEquals(lobbyPutDTO.getName(), savedLobby.getName());
    }

    @Test
    void updateLobby_noUpdates_success() {
        // given
        assertTrue(lobbyRepository.findAll().isEmpty());

        Lobby testLobby = new Lobby(1234, "this is a new lobby");
        testLobby.setPublicAccess(true);

        Player player1 = new Player("123", "asdf", testLobby);
        Player player2 = new Player("234", "jklö", testLobby);
        player1.setOwnedLobby(testLobby);
        testLobby.setOwner(player1);

        testLobby.setPlayers(new ArrayList<>(Arrays.asList(player1, player2)));

        Lobby savedLobby = lobbyRepository.saveAndFlush(testLobby);

        assertEquals(testLobby.getName(), savedLobby.getName());

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();

        Lobby lobby = lobbyService.updateLobby(savedLobby, lobbyPutDTO);
        Map<String, Boolean> updates = lobby.getUpdatedFields();
        assertFalse(updates.get("publicAccess"));
        assertFalse(updates.get("mode"));
        assertFalse(updates.get("name"));

        // then
        assertFalse(updates.get("publicAccess"));
        assertFalse(updates.get("mode"));
        assertFalse(updates.get("name"));
        assertEquals(testLobby.getName(), savedLobby.getName());
    }

    @Test
    void removeLobby_success() {
        assertTrue(lobbyRepository.findAll().isEmpty());

        Lobby testLobby = new Lobby(123, "this is a new lobby");
        testLobby.setPublicAccess(true);

        Player player1 = new Player("123", "asdf", testLobby);
        Player player2 = new Player("234", "jklö", testLobby);
        player1.setOwnedLobby(testLobby);
        testLobby.setOwner(player1);

        testLobby.setPlayers(new ArrayList<>(Arrays.asList(player1, player2)));

        Lobby savedLobby = lobbyRepository.saveAndFlush(testLobby);

        assertEquals(testLobby.getName(), savedLobby.getName());

        lobbyService.removeLobby(savedLobby);
        assertNull(lobbyRepository.findByCode(123));
    }
}
