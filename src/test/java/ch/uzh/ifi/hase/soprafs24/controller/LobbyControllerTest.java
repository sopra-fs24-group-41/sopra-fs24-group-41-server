package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LobbyControllerTest
 * This is a WebMvcTest which allows to test the LobbyController i.e. GET/POST request
 * without actually sending them over the network. This tests if the LobbyController works.
 */
@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @MockBean
    private UserService userService;

    @MockBean
    private PlayerService playerService;

    @Test
    public void givenLobbies_whenGetLobbies_thenReturnJsonArray() throws Exception {
        // given
        Lobby testLobby = new Lobby(1234, "test Lobby");
        testLobby.setPublicAccess(true);
        testLobby.setMode(GameMode.STANDARD);

        Player testPlayer1 = new Player("123", "testplayer", null);
        testPlayer1.setId(5L);
        testPlayer1.setPoints(32);
        // no value for AvailableWords set

        Player testPlayer2 = new Player("643", "anothertestplayer", null);
        testPlayer2.setId(4L);
        testPlayer2.setPoints(54);

        User testUser1 = new User();
        testUser1.setPassword("testPassword");
        testUser1.setUsername("firstname@lastname");
        testUser1.setStatus(UserStatus.OFFLINE);
        testUser1.setToken("1");
        testUser1.setId(1L);

        User testUser2 = new User();
        testUser2.setPassword("testPassword2");
        testUser2.setUsername("firstname@lastname2");
        testUser2.setStatus(UserStatus.OFFLINE);
        testUser2.setToken("2");
        testUser2.setId(2L);

        testUser1.setPlayer(testPlayer1);
        testPlayer1.setUser(testUser1);

        testUser2.setPlayer(testPlayer2);
        testPlayer2.setUser(testUser2);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testPlayer2.setLobby(testLobby);
        testLobby.setPlayers(Arrays.asList(testPlayer1, testPlayer2));

        List<Lobby> allLobbies = Collections.singletonList(testLobby);

        given(lobbyService.getPublicLobbies()).willReturn(allLobbies);

        // when
        MockHttpServletRequestBuilder getRequest = get("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is((int)testLobby.getCode())))
                .andExpect(jsonPath("$[0].name", is(testLobby.getName())))
                .andExpect(jsonPath("$[0].publicAccess", is(testLobby.getPublicAccess())))
                .andExpect(jsonPath("$[0].status", is(testLobby.getStatus().toString())))
                .andExpect(jsonPath("$[0].mode", is(testLobby.getMode().toString())))
                .andExpect(jsonPath("$[0].owner.id", is((int)testLobby.getOwner().getId())))
                .andExpect(jsonPath("$[0].owner.name", is(testLobby.getOwner().getName())))
                .andExpect(jsonPath("$[0].owner.points", is((int)testLobby.getOwner().getPoints())))
                .andExpect(jsonPath("$[0].owner.user.id", is(testLobby.getOwner().getUser().getId().intValue())))
                .andExpect(jsonPath("$[0].owner.user.username", is(testLobby.getOwner().getUser().getUsername())))
                .andExpect(jsonPath("$[0].owner.user.status", is(testLobby.getOwner().getUser().getStatus().toString())))
                .andExpect(jsonPath("$[0].owner.user.profilePicture", is(testLobby.getOwner().getUser().getProfilePicture())))
                .andExpect(jsonPath("$[0].players", hasSize(2)));
    }

    @Test
    public void givenLobbies_validCode_thenReturnsLobby() throws Exception {
        // given
        Lobby testLobby = new Lobby(1234, "test Lobby");
        testLobby.setPublicAccess(true);
        testLobby.setMode(GameMode.STANDARD);

        Player testPlayer1 = new Player("123", "testplayer", null);
        testPlayer1.setId(5L);
        testPlayer1.setPoints(32);
        // no value for AvailableWords set

        Player testPlayer2 = new Player("643", "anothertestplayer", null);
        testPlayer2.setId(4L);
        testPlayer2.setPoints(54);

        User testUser1 = new User();
        testUser1.setPassword("testPassword");
        testUser1.setUsername("firstname@lastname");
        testUser1.setStatus(UserStatus.OFFLINE);
        testUser1.setToken("1");
        testUser1.setId(1L);

        User testUser2 = new User();
        testUser2.setPassword("testPassword2");
        testUser2.setUsername("firstname@lastname2");
        testUser2.setStatus(UserStatus.OFFLINE);
        testUser2.setToken("2");
        testUser2.setId(2L);

        testUser1.setPlayer(testPlayer1);
        testPlayer1.setUser(testUser1);

        testUser2.setPlayer(testPlayer2);
        testPlayer2.setUser(testUser2);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testPlayer2.setLobby(testLobby);
        testLobby.setPlayers(Arrays.asList(testPlayer1, testPlayer2));

        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willReturn(testLobby);

        // when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is((int)testLobby.getCode())))
                .andExpect(jsonPath("$.name", is(testLobby.getName())))
                .andExpect(jsonPath("$.publicAccess", is(testLobby.getPublicAccess())))
                .andExpect(jsonPath("$.status", is(testLobby.getStatus().toString())))
                .andExpect(jsonPath("$.mode", is(testLobby.getMode().toString())))
                .andExpect(jsonPath("$.owner.id", is((int)testLobby.getOwner().getId())))
                .andExpect(jsonPath("$.owner.name", is(testLobby.getOwner().getName())))
                .andExpect(jsonPath("$.owner.points", is((int)testLobby.getOwner().getPoints())))
                .andExpect(jsonPath("$.owner.user.id", is(testLobby.getOwner().getUser().getId().intValue())))
                .andExpect(jsonPath("$.owner.user.username", is(testLobby.getOwner().getUser().getUsername())))
                .andExpect(jsonPath("$.owner.user.status", is(testLobby.getOwner().getUser().getStatus().toString())))
                .andExpect(jsonPath("$.owner.user.profilePicture", is(testLobby.getOwner().getUser().getProfilePicture())))
                .andExpect(jsonPath("$.players", hasSize(2)));
    }

    @Test
    public void givenLobbies_badlyFormattedCode_throwsBadRequestError() throws Exception {
        // when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/onetwothreefour")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void givenLobbies_unknownCode_throwsNotFoundException() throws Exception {
        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/2345")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    public void createLobbyByUser_validToken_thenLobbyAndPlayerTokenReturned() throws Exception {
        // given
        Lobby testLobby = new Lobby(1234, "testplayer's Lobby");
        testLobby.setPublicAccess(true);
        testLobby.setMode(GameMode.STANDARD);

        Player testPlayer1 = new Player("123", "testplayer", null);
        testPlayer1.setId(5L);
        testPlayer1.setPoints(32);
        // no value for AvailableWords set

        User testUser1 = new User();
        testUser1.setPassword("testPassword");
        testUser1.setUsername("firstname@lastname");
        testUser1.setStatus(UserStatus.OFFLINE);
        testUser1.setToken("1254");
        testUser1.setId(1L);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testLobby.setPlayers(List.of(testPlayer1));

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setPublicAccess(true);

        given(userService.checkToken(Mockito.any())).willReturn(testUser1);
        given(lobbyService.createLobbyFromUser(Mockito.any(), Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder postRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("userToken", "1254");

        //then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerToken", is(testPlayer1.getToken())))
                .andExpect(jsonPath("$.lobby.code", is((int)testPlayer1.getLobby().getCode())))
                .andExpect(jsonPath("$.lobby.name", is(testPlayer1.getLobby().getName())))
                .andExpect(jsonPath("$.lobby.publicAccess", is(testPlayer1.getLobby().getPublicAccess())))
                .andExpect(jsonPath("$.lobby.status", is(testPlayer1.getLobby().getStatus().toString())))
                .andExpect(jsonPath("$.lobby.mode", is(testPlayer1.getLobby().getMode().toString())));
    }

    @Test
    public void createLobbyByUser_InvalidToken_throwsExceptionUnauthorized() throws Exception {
        // given
        given(userService.checkToken(Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user token has insufficient access rights"));

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setPublicAccess(true);

        // when
        MockHttpServletRequestBuilder postRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("userToken", "42");

        //then
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void joinLobbyByUser_validToken_thenLobbyAndPlayerTokenReturned() throws Exception {
        // given
        Lobby testLobby = new Lobby(1234, "testplayer's Lobby");
        testLobby.setPublicAccess(true);
        testLobby.setMode(GameMode.STANDARD);

        Player testPlayer1 = new Player("123", "testplayer", null);
        testPlayer1.setId(5L);
        testPlayer1.setPoints(32);
        // no value for AvailableWords set

        User testUser1 = new User();
        testUser1.setPassword("testPassword");
        testUser1.setUsername("firstname@lastname");
        testUser1.setStatus(UserStatus.OFFLINE);
        testUser1.setToken("1254");
        testUser1.setId(1L);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testLobby.setPlayers(List.of(testPlayer1));

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setPublicAccess(true);

        given(userService.checkToken(Mockito.any())).willReturn(testUser1);
        given(lobbyService.joinLobbyFromUser(Mockito.any(), Mockito.anyLong())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/1234/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("userToken", "1254");

        //then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerToken", is(testPlayer1.getToken())))
                .andExpect(jsonPath("$.lobby.code", is((int)testPlayer1.getLobby().getCode())))
                .andExpect(jsonPath("$.lobby.name", is(testPlayer1.getLobby().getName())))
                .andExpect(jsonPath("$.lobby.publicAccess", is(testPlayer1.getLobby().getPublicAccess())))
                .andExpect(jsonPath("$.lobby.status", is(testPlayer1.getLobby().getStatus().toString())))
                .andExpect(jsonPath("$.lobby.mode", is(testPlayer1.getLobby().getMode().toString())));
    }

    @Test
    public void joinLobbyByUser_InvalidLobbyCode_throwsNotFoundException() throws Exception {
        // given
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1254");
        testUser.setId(1L);

        given(userService.checkToken(Mockito.any())).willReturn(testUser);
        given(lobbyService.joinLobbyFromUser(Mockito.any(), Mockito.anyLong()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby code not found"));

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setPublicAccess(true);

        // when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/6543/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("userToken", "1254");

        //then
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    public void removePlayerNotOwner_validInputs_success() throws Exception {
        // given
        Lobby testLobby = new Lobby(1234, "testplayer's Lobby");
        testLobby.setPublicAccess(true);

        Player testPlayer1 = new Player("123", "testplayer", testLobby);
        testPlayer1.setId(5L);
        testPlayer1.setPoints(32);

        Player testPlayer2 = new Player("234", "testplayer2", testLobby);
        testPlayer2.setId(6L);
        testPlayer2.setPoints(54);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testPlayer2.setLobby(testLobby);
        testLobby.setPlayers(List.of(testPlayer1, testPlayer2));

        given(playerService.checkToken(Mockito.any())).willReturn(testPlayer2);
        Mockito.doNothing().when(playerService).removePlayer(Mockito.any());

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/lobbies/1234/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", "234");

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        Mockito.verify(playerService, Mockito.times(1)).checkToken(Mockito.any());
        Mockito.verify(playerService, Mockito.times(1)).removePlayer(Mockito.any());
    }

    @Test
    public void removePlayerOwner_validInputs_success() throws Exception {
        // given
        Lobby testLobby = new Lobby(1234, "testplayer's Lobby");
        testLobby.setPublicAccess(true);

        Player testPlayer1 = new Player("123", "testplayer", testLobby);
        testPlayer1.setId(5L);
        testPlayer1.setPoints(32);

        Player testPlayer2 = new Player("234", "testplayer2", testLobby);
        testPlayer2.setId(6L);
        testPlayer2.setPoints(54);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testPlayer2.setLobby(testLobby);
        testLobby.setPlayers(List.of(testPlayer1, testPlayer2));

        given(playerService.checkToken(Mockito.any())).willReturn(testPlayer1);
        Mockito.doNothing().when(playerService).removePlayer(Mockito.any());
        Mockito.doNothing().when(lobbyService).removeLobby(Mockito.any());

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/lobbies/1234/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", "123");

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        Mockito.verify(playerService, Mockito.times(1)).checkToken(Mockito.any());
        Mockito.verify(playerService, Mockito.times(2)).removePlayer(Mockito.any());
        Mockito.verify(lobbyService, Mockito.times(1)).removeLobby(Mockito.any());
    }

    @Test
    public void removePlayer_noToken_throwsBadRequestError() throws Exception {
        // given
        Lobby testLobby = new Lobby(1234, "testplayer's Lobby");
        testLobby.setPublicAccess(true);

        Player testPlayer1 = new Player("123", "testplayer", testLobby);
        testPlayer1.setId(5L);
        testPlayer1.setPoints(32);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testLobby.setPlayers(List.of(testPlayer1));

        given(playerService.checkToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/lobbies/1234/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void removePlayer_invalidCode_throwsBadRequestError() throws Exception {
        // given
        Lobby testLobby = new Lobby(1234, "testplayer's Lobby");
        testLobby.setPublicAccess(true);

        Player testPlayer1 = new Player("123", "testplayer", testLobby);
        testPlayer1.setId(5L);
        testPlayer1.setPoints(32);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testLobby.setPlayers(List.of(testPlayer1));

        given(playerService.checkToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/lobbies/4541/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", "123");

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void removePlayer_badlyFormattedCode_throwsBadRequestError() throws Exception {
        // given
        Lobby testLobby = new Lobby(1234, "testplayer's Lobby");
        testLobby.setPublicAccess(true);

        Player testPlayer1 = new Player("123", "testplayer", testLobby);
        testPlayer1.setId(5L);
        testPlayer1.setPoints(32);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testLobby.setPlayers(List.of(testPlayer1));

        given(playerService.checkToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/lobbies/four23one/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", "123");

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isBadRequest());
    }

    /**
     * helper method that translates an object into a JSON string
     * @param object object to be translated
     * @return JSON string of object
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e));
        }
    }

}
