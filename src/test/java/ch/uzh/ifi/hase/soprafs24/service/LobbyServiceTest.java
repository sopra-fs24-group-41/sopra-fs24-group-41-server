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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        testLobby.setPlayers(new ArrayList<>(Arrays.asList(testPlayer)));

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);
        Mockito.when(lobbyRepository.saveAndFlush(Mockito.any())).thenReturn(testLobby);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
    }

    @Test
    public void getPublicLobbies_returnsPublicLobbies() {
        Mockito.when(lobbyRepository.findAllByPublicAccess(true)).thenReturn(Collections.singletonList(testLobby));

        List<Lobby> foundLobbies = lobbyService.getPublicLobbies();

        // then
        Mockito.verify(lobbyRepository, Mockito.times(1)).findAllByPublicAccess(Mockito.anyBoolean());
        assertArrayEquals(Collections.singletonList(testLobby).toArray(), foundLobbies.toArray());
    }

    @Test
    public void getLobby_validCode_returnsLobby() {
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(testLobby);
        Lobby foundLobby = lobbyService.getLobbyByCode(1234);

        // then
        Mockito.verify(lobbyRepository, Mockito.times(1)).findByCode(Mockito.anyLong());
        assertEquals(testLobby, foundLobby);
    }

    @Test
    public void getLobby_invalidCode_throwsNotFoundException() {
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> lobbyService.getLobbyByCode(Mockito.anyLong()));
    }

    @Test
    public void createLobbyByUser_validInputs_success() {
        Player createdPlayer = lobbyService.createLobbyFromUser(testUser, true);

        // then
        Mockito.verify(lobbyRepository, Mockito.times(1)).saveAndFlush(Mockito.any());

        assertEquals(testLobby.getCode(), createdPlayer.getLobby().getCode());
        assertEquals(testLobby.getName(), createdPlayer.getLobby().getName());
        assertEquals(testLobby.getPublicAccess(), createdPlayer.getLobby().getPublicAccess());
        assertEquals(testLobby.getStatus(), createdPlayer.getLobby().getStatus());
        assertEquals(testLobby.getMode(), createdPlayer.getLobby().getMode());
        assertEquals(testLobby.getOwner(), createdPlayer.getLobby().getOwner());
        assertArrayEquals(testLobby.getPlayers().toArray(), createdPlayer.getLobby().getPlayers().toArray());
    }

    @Test
    public void joinLobbyByUser_validInputs_success() {
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(testLobby);
        Player createdPlayer = lobbyService.joinLobbyFromUser(testUser, testLobby.getCode());

        // then
        assertEquals(testLobby.getCode(), createdPlayer.getLobby().getCode());
        assertEquals(testLobby.getName(), createdPlayer.getLobby().getName());
        assertEquals(testLobby.getPublicAccess(), createdPlayer.getLobby().getPublicAccess());
        assertEquals(testLobby.getStatus(), createdPlayer.getLobby().getStatus());
        assertEquals(testLobby.getMode(), createdPlayer.getLobby().getMode());
        assertEquals(testLobby.getOwner(), createdPlayer.getLobby().getOwner());
        assertEquals(testLobby.getPlayers(), createdPlayer.getLobby().getPlayers());
    }

    @Test
    public void joinLobbyByUser_invalidCode_throwsNotFoundError() {
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyFromUser(testUser, 232));
    }

    @Test
    public void joinLobbyByUser_lobbyNotPregame_throwsForbiddenError() {
        testLobby.setStatus(LobbyStatus.INGAME);
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(testLobby);

        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyFromUser(testUser, testLobby.getCode()));
    }

    @Test
    public void removeLobby_success() {
        // TODO: to be implemented
    }
}
