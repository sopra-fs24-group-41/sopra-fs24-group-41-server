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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LobbyServiceTest {

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerService playerService;

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
    void getPublicLobbies_returnsPublicLobbies() {
        Mockito.when(lobbyRepository.findAllByPublicAccess(true)).thenReturn(Collections.singletonList(testLobby));

        List<Lobby> foundLobbies = lobbyService.getPublicLobbies();

        // then
        Mockito.verify(lobbyRepository, Mockito.times(1)).findAllByPublicAccess(Mockito.anyBoolean());
        assertArrayEquals(Collections.singletonList(testLobby).toArray(), foundLobbies.toArray());
    }

    @Test
    void getLobby_validCode_returnsLobby() {
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(testLobby);
        Lobby foundLobby = lobbyService.getLobbyByCode(1234);

        // then
        Mockito.verify(lobbyRepository, Mockito.times(1)).findByCode(Mockito.anyLong());
        assertEquals(testLobby, foundLobby);
    }

    @Test
    void getLobby_invalidCode_throwsNotFoundException() {
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> lobbyService.getLobbyByCode(Mockito.anyLong()));
    }

    @Test
    void createLobbyByUser_validInputs_success() {
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
    void createLobbyFromUser_publicAccessFalse_success() {
        Player createdPlayer = lobbyService.createLobbyFromUser(testUser, false);
        assertFalse(createdPlayer.getLobby().getPublicAccess());
    }

    @Test
    void updateLobby_validInput_success() {
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setPublicAccess(true);
        lobbyPutDTO.setMode(GameMode.FUSIONFRENZY);
        lobbyPutDTO.setName("new name");

        Map<String, Boolean> updates = lobbyService.updateLobby(testLobby, lobbyPutDTO);
        assertEquals(true, updates.get("publicAccess"));
        assertEquals(true, updates.get("mode"));
        assertEquals(true, updates.get("name"));
        assertEquals(lobbyPutDTO.getPublicAccess(), testLobby.getPublicAccess());
        assertEquals(lobbyPutDTO.getMode(), testLobby.getMode());
        assertEquals(lobbyPutDTO.getName(), testLobby.getName());
    }

    @Test
    void updateLobby_noUpdates_success() {
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        Map<String, Boolean> updates = lobbyService.updateLobby(testLobby, lobbyPutDTO);
        assertFalse(updates.get("publicAccess"));
        assertFalse(updates.get("mode"));
        assertFalse(updates.get("name"));
    }

    @Test
    void joinLobbyByUser_validInputs_success() {
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
    void joinLobbyByUser_invalidCode_throwsNotFoundError() {
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyFromUser(testUser, 232));
    }

    @Test
    void joinLobbyByUser_lobbyNotPregame_throwsForbiddenError() {
        testLobby.setStatus(LobbyStatus.INGAME);
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(testLobby);
        long lobbyCode = testLobby.getCode();

        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyFromUser(testUser, lobbyCode));
    }

    @Test
    void joinLobbyAnonymous_validInput_success() {
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(testLobby);
        Player createdPlayer = lobbyService.joinLobbyAnonymous("testPlayer2", testLobby.getCode());

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
    void joinLobbyAnonymous_invalidCode_throwsNotFoundError() {
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyAnonymous("testPlayer2", 232));
    }

    @Test
    void joinLobbyAnonymous_lobbyNotPregame_throwsForbiddenError() {
        testLobby.setStatus(LobbyStatus.INGAME);
        Mockito.when(lobbyRepository.findByCode(Mockito.anyLong())).thenReturn(testLobby);
        long lobbyCode = testLobby.getCode();

        assertThrows(ResponseStatusException.class, () -> lobbyService.joinLobbyAnonymous("testPlayer2", lobbyCode));
    }

    @Test
    void removeLobby_success() {
        // given
        Player testPlayer2 = new Player("234", "testPlayer2", testLobby);
        testLobby.getPlayers().add(testPlayer2);

        Mockito.doNothing().when(lobbyRepository).delete(Mockito.any());
        Mockito.doNothing().when(playerService).removePlayer(Mockito.any());

        // when
        lobbyService.removeLobby(testLobby);

        // then
        Mockito.verify(playerService, Mockito.times(2)).removePlayer(Mockito.any());
        Mockito.verify(lobbyRepository, Mockito.times(1)).delete(Mockito.any());
    }

    @Test
    void removeLobby_noOwnerOrPlayers_success() {
        testLobby.setOwner(null);
        testLobby.setPlayers(null);
        assertDoesNotThrow(() -> lobbyService.removeLobby(testLobby));
    }
}
