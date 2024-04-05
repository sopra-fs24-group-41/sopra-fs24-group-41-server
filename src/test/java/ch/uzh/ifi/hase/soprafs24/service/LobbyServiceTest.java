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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTest {

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LobbyService lobbyService;

    private User testUser;

    private Lobby testLobby;

    private Player testPlayer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testLobby = new Lobby(1234, "test Lobby");
        testLobby.setMode(GameMode.STANDARD);

        testPlayer = new Player("123", "testplayer", null);
        testPlayer.setPoints(32);
        // no value for AvailableWords set

        testUser = new User();
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

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);
        Mockito.when(lobbyRepository.saveAndFlush(Mockito.any())).thenReturn(testLobby);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
    }

    @Test
    public void createLobbyByUser_validInputs_success() {
        Lobby createdLobby = lobbyService.createLobbyFromUser(testUser, true);

        // then
        Mockito.verify(lobbyRepository, Mockito.times(1)).saveAndFlush(Mockito.any());

        assertEquals(testLobby.getCode(), createdLobby.getCode());
        assertEquals(testLobby.getName(), createdLobby.getName());
        assertEquals(testLobby.getPublicAccess(), createdLobby.getPublicAccess());
        assertEquals(testLobby.getStatus(), createdLobby.getStatus());
        assertEquals(testLobby.getMode(), createdLobby.getMode());
        assertEquals(testLobby.getOwner(), createdLobby.getOwner());
        assertEquals(testLobby.getPlayers(), createdLobby.getPlayers());
    }
}
