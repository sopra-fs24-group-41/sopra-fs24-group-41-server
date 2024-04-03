package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@SpringBootTest
public class LobbyServiceIntegrationTest {

    @Qualifier("lobbyRepository")
    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private LobbyService lobbyService;

    @BeforeEach
    public void setup() {
        lobbyRepository.deleteAll();
    }

    @Test
    public void createLobbyByUser_validInputs_success() {
        // given
        assertNull(lobbyRepository.findByCode(1234));

        Lobby testLobby = new Lobby();
        testLobby.setPublicAccess(true);
        testLobby.setMode(GameMode.STANDARD);

        Player testPlayer = new Player("123", "testplayer", null);
        testPlayer.setPoints(32);
        // no value for AvailableWords set

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");

        testUser.setPlayer(testPlayer);
        testPlayer.setUser(testUser);

        testLobby.setOwner(testPlayer);
        testPlayer.setOwnedLobby(testLobby);

        testPlayer.setLobby(testLobby);
        testLobby.setPlayers(Collections.singletonList(testPlayer));

        // when
        Lobby createdLobby = lobbyService.createLobbyFromUser(testUser);

        //then
        assertNotNull(createdLobby.getName());
        assertEquals(testLobby.getPublicAccess(), createdLobby.getPublicAccess());
        assertNotNull(createdLobby.getStatus());
        assertEquals(testLobby.getOwner().getUser(), createdLobby.getOwner().getUser());
    }
}
