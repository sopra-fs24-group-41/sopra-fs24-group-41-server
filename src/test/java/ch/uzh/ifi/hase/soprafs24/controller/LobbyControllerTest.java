package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.hamcrest.Matchers.*;
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
class LobbyControllerTest {
    private Lobby testLobby;
    private List<Lobby> allLobbies;
    private User testUser1;
    private Player testPlayer1;
    private User testUser2;
    private Player testPlayer2;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @MockBean
    private UserService userService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private GameService gameService;

    @MockBean
    private APIService apiService;

    @MockBean
    private CombinationService combinationService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    public void setup() {
        // User/Player 1
        testUser1 = new User();
        testUser1.setPassword("testPassword");
        testUser1.setUsername("firstname@lastname");
        testUser1.setStatus(UserStatus.OFFLINE);
        testUser1.setToken("11");
        testUser1.setId(1L);

        testPlayer1 = new Player("33", "testplayer", null);
        testPlayer1.setId(3L);
        testPlayer1.setPoints(30);

        testUser1.setPlayer(testPlayer1);
        testPlayer1.setUser(testUser1);

        // User/Player 2
        testUser2 = new User();
        testUser2.setPassword("testPassword2");
        testUser2.setUsername("firstname@lastname2");
        testUser2.setStatus(UserStatus.OFFLINE);
        testUser2.setToken("22");
        testUser2.setId(2L);

        testPlayer2 = new Player("44", "anothertestplayer", null);
        testPlayer2.setId(4L);
        testPlayer2.setPoints(40);

        testUser2.setPlayer(testPlayer2);
        testPlayer2.setUser(testUser2);

        // Lobby
        testLobby = new Lobby(1234, "test Lobby");
        testLobby.setPublicAccess(true);
        testLobby.setMode(GameMode.STANDARD);

        testPlayer1.setOwnedLobby(testLobby);
        testPlayer1.setLobby(testLobby);
        testPlayer2.setLobby(testLobby);

        testLobby.setOwner(testPlayer1);
        testLobby.setPlayers(Arrays.asList(testPlayer1, testPlayer2));

        allLobbies = Collections.singletonList(testLobby);
    }

    @Test
    void givenLobbies_whenGetLobbies_thenReturnJsonArray() throws Exception {
        // given
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
    void givenNoLobbies_validCode_thenReturnEmptyList() throws Exception {
        // given
        given(lobbyService.getPublicLobbies()).willReturn(new ArrayList<>());

        // when
        MockHttpServletRequestBuilder getRequest = get("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void givenLobbies_validCode_thenReturnsLobby() throws Exception {
        //given
        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willReturn(testLobby);

        // when
        MockHttpServletRequestBuilder getRequest = get(String.format("/lobbies/%s", testLobby.getCode()))
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
    void givenLobbies_badlyFormattedCode_throwsBadRequestError() throws Exception {
        // when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/onetwothreefour")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isBadRequest());
    }

    @Test
    void givenLobbies_unknownCode_throwsNotFoundException() throws Exception {
        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/2345")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    void createLobbyByUser_validToken_thenLobbyAndPlayerTokenReturned() throws Exception {
        // given
        testUser1.setPlayer(null);
        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setPublicAccess(true);

        given(userService.checkToken(Mockito.any())).willReturn(testUser1);
        given(lobbyService.createLobbyFromUser(Mockito.any(), Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder postRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("userToken", testUser1.getToken());

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
    void createLobbyByUser_InvalidToken_throwsExceptionUnauthorized() throws Exception {
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
    void updateLobby_validInputs_thenUpdatedLobbyReturned() throws Exception {
        // given
        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willReturn(testLobby);
        testLobby.setMode(GameMode.FUSIONFRENZY);
        testLobby.setName("new name");
        testLobby.setPublicAccess(false);
        testLobby.setUpdatedMode(true);
        testLobby.setUpdatedName(true);
        testLobby.setUpdatedPublicAccess(true);

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setPublicAccess(false);
        lobbyPutDTO.setMode(GameMode.FUSIONFRENZY);
        lobbyPutDTO.setName("new name");

        given(lobbyService.updateLobby(Mockito.any(), Mockito.any())).willReturn(testLobby);

        // when
        MockHttpServletRequestBuilder putRequest = put("/lobbies/"+testLobby.getCode())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutDTO))
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(lobbyPutDTO.getName())))
                .andExpect(jsonPath("$.publicAccess", is(lobbyPutDTO.getPublicAccess())))
                .andExpect(jsonPath("$.mode", is(lobbyPutDTO.getMode().toString())));

    }

    @Test
    void updateLobby_InvalidToken_throwsForbiddenException() throws Exception {
        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willReturn(testLobby);

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setPublicAccess(false);
        lobbyPutDTO.setMode(GameMode.FUSIONFRENZY);
        lobbyPutDTO.setName("new name");

        // when
        MockHttpServletRequestBuilder putRequest = put("/lobbies/"+testLobby.getCode())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutDTO))
                .header("playerToken", testPlayer1.getToken()+"123");

        //then
        mockMvc.perform(putRequest).andExpect(status().isForbidden());
    }

    @Test
    void updateLobby_InvalidCode_throwsBadRequestException() throws Exception {
        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willReturn(testLobby);

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setPublicAccess(false);
        lobbyPutDTO.setMode(GameMode.FUSIONFRENZY);
        lobbyPutDTO.setName("new name");

        // when
        MockHttpServletRequestBuilder putRequest = put("/lobbies/asdf")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutDTO))
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(putRequest).andExpect(status().isBadRequest());
    }

    @Test
    void updateLobby_NotOwner_throwsForbiddenException() throws Exception {
        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willReturn(testLobby);

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setPublicAccess(false);
        lobbyPutDTO.setMode(GameMode.FUSIONFRENZY);
        lobbyPutDTO.setName("new name");

        // when
        MockHttpServletRequestBuilder putRequest = put("/lobbies/"+testLobby.getCode())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutDTO))
                .header("playerToken", testPlayer2.getToken());

        //then
        mockMvc.perform(putRequest).andExpect(status().isForbidden());
    }

    @Test
    void updateLobby_InvalidGameMode_throwsBadRequestException() throws Exception {
        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willReturn(testLobby);

        String lobbyPutRequestString = "{\"publicAccess\": false, \"name\": \"new name\", \"mode\": \"unknown\"}";

        // when
        MockHttpServletRequestBuilder putRequest = put("/lobbies/"+testLobby.getCode())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(lobbyPutRequestString)
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(putRequest).andExpect(status().isBadRequest());
    }

    @Test
    void joinLobbyByUser_validToken_thenLobbyAndPlayerTokenReturned() throws Exception {
        // given
        testUser1.setPlayer(null);
        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setPublicAccess(true);

        given(userService.checkToken(Mockito.any())).willReturn(testUser1);
        given(lobbyService.joinLobbyFromUser(Mockito.any(), Mockito.anyLong())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder postRequest = post(String.format("/lobbies/%s/players", testLobby.getCode()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("userToken", testUser1.getToken());

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
    void joinLobbyByUser_InvalidLobbyCode_throwsNotFoundException() throws Exception {
        // given
        testUser1.setPlayer(null);
        given(userService.checkToken(Mockito.any())).willReturn(testUser1);
        given(lobbyService.joinLobbyFromUser(Mockito.any(), Mockito.anyLong()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby code not found"));

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setPublicAccess(true);

        // when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/6543/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("userToken", testUser1.getToken());

        //then
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    void getPlayers_validInputs_thenReturnsPlayers() throws Exception {
        // given
        given(lobbyService.getLobbyByCode(Mockito.anyLong())).willReturn(testLobby);

        // when
        MockHttpServletRequestBuilder getRequest = get(String.format("/lobbies/%s/players", testLobby.getCode()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(testLobby.getPlayers().stream().map(Player::getId).map(Long::intValue).toArray())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(testLobby.getPlayers().stream().map(Player::getName).toArray())))
                .andExpect(jsonPath("$[*].points", containsInAnyOrder(testLobby.getPlayers().stream().map(Player::getPoints).map(Long::intValue).toArray())));
//                .andExpect(jsonPath("$[*].user.username", containsInAnyOrder(testLobby.getPlayers().stream().map(Player::getUser).map(User::getUsername).toArray())))
//                .andExpect(jsonPath("$[*].user.status", containsInAnyOrder(testLobby.getPlayers().stream().map(Player::getUser).map(User::getStatus).map(UserStatus::toString).toArray())))
//                .andExpect(jsonPath("$[*].user.profilePicture", containsInAnyOrder(testLobby.getPlayers().stream().map(Player::getUser).map(User::getProfilePicture).toArray())));
    }

    @Test
    void getPlayer_validInputs_thenReturnsPlayer() throws Exception {
        // given
        given(playerService.findPlayerByToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder getRequest = get(String.format("/lobbies/%s/players/%s", testLobby.getCode(), testPlayer1.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("playerToken", testPlayer1.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)testPlayer1.getId())))
                .andExpect(jsonPath("$.name", is(testPlayer1.getName())))
                .andExpect(jsonPath("$.points", is((int)testPlayer1.getPoints())));
//                .andExpect(jsonPath("$.user.username", is(testPlayer1.getUser().getUsername())))
//                .andExpect(jsonPath("$.user.status", is(testPlayer1.getUser().getStatus().toString())))
//                .andExpect(jsonPath("$.user.profilePicture", is(testPlayer1.getUser().getProfilePicture())));
    }

    @Test
    void startGame_standard_success() throws Exception {
        // given
        given(lobbyService.getLobbyByCode(testLobby.getCode())).willReturn(testLobby);

        // when
        MockHttpServletRequestBuilder postRequest = post(String.format("/lobbies/%s/games", testLobby.getCode()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());
    }

    @Test
    void play_standard_success() throws Exception {
        // given
        given(playerService.findPlayerByToken(testPlayer1.getToken())).willReturn(testPlayer1);
        given(lobbyService.getLobbyByCode(testLobby.getCode())).willReturn(testLobby);
        List<Word> words = new ArrayList<>();
        words.add(new Word("water"));
        words.add(new Word("fire"));

        testPlayer1.addWords(words);

        // when
        MockHttpServletRequestBuilder putRequest = put(String.format("/lobbies/%s/players/%s", testLobby.getCode(), testPlayer1.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(new ArrayList<Word>()))
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", is((int) testPlayer1.getPoints())))
                .andExpect(jsonPath("$.playerWords[*].word.name", containsInAnyOrder(testPlayer1.getWords().stream().map(Word::getName).toArray(String[]::new))))
                .andExpect(jsonPath("$.targetWord", is(testPlayer1.getTargetWord())));
    }

    @Test
    void play_invalidId_throwsExceptionUnauthorized() throws Exception {
        // given
        given(playerService.findPlayerByToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder putRequest = put(String.format("/lobbies/%s/players/321", testLobby.getCode()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(new ArrayList<Word>()))
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(putRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    void getCombination_validInputs_thenReturnsCombination() throws Exception {
        // given
        given(combinationService.findCombination(Mockito.any(), Mockito.any()))
                .willReturn(new Combination(new Word("water"), new Word("fire"), new Word("steam")));

        // when
        MockHttpServletRequestBuilder getRequest = get(String.format("/combinations/%s/%s", "water", "fire"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("method", "find");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("steam")));
    }

    @Test
    void removePlayerNotOwner_validInputs_success() throws Exception {
        // given
        given(playerService.findPlayerByToken(Mockito.any())).willReturn(testPlayer2);
        Mockito.doNothing().when(playerService).removePlayer(Mockito.any());

        // when
        MockHttpServletRequestBuilder deleteRequest = delete(String.format("/lobbies/%s/players/%s", testLobby.getCode(), testPlayer2.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("playerToken", testPlayer2.getToken());

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());

        Mockito.verify(playerService, Mockito.times(1)).findPlayerByToken(Mockito.any());
        Mockito.verify(playerService, Mockito.times(1)).removePlayer(Mockito.any());
    }

    @Test
    void removePlayerOwner_validInputs_success() throws Exception {
        // given
        given(playerService.findPlayerByToken(Mockito.any())).willReturn(testPlayer1);
        Mockito.doNothing().when(lobbyService).removeLobby(Mockito.any());

        // when
        MockHttpServletRequestBuilder deleteRequest = delete(String.format("/lobbies/%s/players/%s", testLobby.getCode(), testPlayer1.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());

        Mockito.verify(playerService, Mockito.times(1)).findPlayerByToken(Mockito.any());
        Mockito.verify(lobbyService, Mockito.times(1)).removeLobby(Mockito.any());
    }

    @Test
    void removePlayer_noToken_throwsBadRequestError() throws Exception {
        // given
        given(playerService.findPlayerByToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete(String.format("/lobbies/%s/players/%s", testLobby.getCode(), testPlayer1.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void removePlayer_invalidCode_throwsBadRequestError() throws Exception {
        // given
        given(playerService.findPlayerByToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete(String.format("/lobbies/5432/players/%s", testPlayer2.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void removePlayer_badlyFormattedCode_throwsBadRequestError() throws Exception {
        // given
        given(playerService.findPlayerByToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete(String.format("/lobbies/4twothree1/players/%s", testPlayer2.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void removePlayer_invalidId_throwsForbiddenException() throws Exception {
        // given
        given(playerService.findPlayerByToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete(String.format("/lobbies/%s/players/321", testLobby.getCode()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    void removePlayer_badlyFormattedId_throwsBadRequestException() throws Exception {
        // given
        given(playerService.findPlayerByToken(Mockito.any())).willReturn(testPlayer1);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete(String.format("/lobbies/%s/players/threetwo", testLobby.getCode()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("playerToken", testPlayer1.getToken());

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isBadRequest());
    }

    /**
     * helper method that translates an object into a JSON string
     *
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
