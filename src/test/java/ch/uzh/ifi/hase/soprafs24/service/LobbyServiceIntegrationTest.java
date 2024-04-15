package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@SpringBootTest
public class LobbyServiceIntegrationTest {

    @Qualifier("lobbyRepository")
    @Autowired
    private LobbyRepository lobbyRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyService lobbyService;

    @BeforeEach
    public void setup() {
        lobbyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createLobbyByUser_validInputs_success() {
        // given
        assertNull(lobbyRepository.findByCode(1234));

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");

        User savedTestUser = userRepository.saveAndFlush(testUser);

        // when
        Player createdPlayer = lobbyService.createLobbyFromUser(savedTestUser, true);

        //then
        assertNotNull(createdPlayer.getName());
        assertNotNull(createdPlayer.getLobby().getName());
        assertEquals(true, createdPlayer.getLobby().getPublicAccess());
        assertNotNull(createdPlayer.getLobby().getStatus());
        assertEquals(savedTestUser, createdPlayer.getLobby().getOwner().getUser());
    }

    @Test
    public void joinLobbyByUser_validInputs_success() {
        // given
        assertNull(lobbyRepository.findByCode(1234));

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");

        User savedTestUser = userRepository.saveAndFlush(testUser);
        Player testPlayer = lobbyService.createLobbyFromUser(savedTestUser, true);

        // when
        Player joinedPlayer = lobbyService.joinLobbyFromUser(savedTestUser, testPlayer.getLobby().getCode());

        //then
        assertNotNull(testPlayer.getName(), joinedPlayer.getName());
        assertNotNull(joinedPlayer.getLobby().getName());
        assertEquals(true, joinedPlayer.getLobby().getPublicAccess());
        assertNotNull(joinedPlayer.getLobby().getStatus());
        assertEquals(testPlayer.getLobby().getCode(), joinedPlayer.getLobby().getCode());
    }

    @Test
    public void joinLobbyByUser_invalidCode_throwsNotFoundException() {
        // given
        assertNull(lobbyRepository.findByCode(234));

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
    public void removeLobby_success() {
        assertNull(lobbyRepository.findByCode(123));

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
