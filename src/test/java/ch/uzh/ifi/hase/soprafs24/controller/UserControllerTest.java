package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTokenPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @Autowired
    private UserController userController;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].username", is(user.getUsername()))).andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("testPassword1234");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
        userLoginPostDTO.setPassword("testPassword1234");
        userLoginPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userLoginPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated()).andExpect(jsonPath("$.id", is(user.getId().intValue()))).andExpect(jsonPath("$.username", is(user.getUsername()))).andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_duplicateUser_throwExceptionConflict() throws Exception {
        UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
        userLoginPostDTO.setPassword("test_password");
        userLoginPostDTO.setUsername("duplicate_username");

        given(userService.createUser(any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Therefore, the user could not be created!"));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userLoginPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isConflict());
    }

    @Test
    public void logInUser_validInput_returnsUserToken() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password1234");
        user.setToken("1");

        UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
        userLoginPostDTO.setUsername("username");
        userLoginPostDTO.setPassword("password1234");

        given(userService.logInUser(any())).willReturn(user);

        MockHttpServletRequestBuilder postRequest = post("/logins").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userLoginPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isOk()).andExpect(jsonPath("$.token", is(user.getToken())));
    }

    @Test
    public void logInUser_nonExistingUsername_throwsException() throws Exception {
        UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
        userLoginPostDTO.setUsername("username");
        userLoginPostDTO.setPassword("password1234");

        given(userService.logInUser(any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find this username."));

        MockHttpServletRequestBuilder postRequest = post("/logins").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userLoginPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    public void logInUser_wrongPassword_throwsException() throws Exception {
        UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
        userLoginPostDTO.setUsername("username");
        userLoginPostDTO.setPassword("wrong_password");

        given(userService.logInUser(any())).willThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong password"));

        MockHttpServletRequestBuilder postRequest = post("/logins").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userLoginPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    public void logOutUser_validToken_success() throws Exception {
        UserTokenPostDTO userTokenPostDTO = new UserTokenPostDTO();
        userTokenPostDTO.setToken("1234");

        MockHttpServletRequestBuilder postRequest = post("/logouts").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userTokenPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isNoContent());
    }

    @Test
    public void logOutUser_nonExistingToken_throwsException() throws Exception {
        UserTokenPostDTO userTokenPostDTO = new UserTokenPostDTO();
        userTokenPostDTO.setToken("1234");

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Logging out unsuccessful: Unknown user (invalid token).")).when(userService).logOutUser(any());

        MockHttpServletRequestBuilder postRequest = post("/logouts").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userTokenPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_Success() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("Okay");
        userPutDTO.setFavourite("Okay");
        userPutDTO.setProfilePicture("BlueFrog");

        User user = new User();
        user.setId(1L);
        user.setUsername("Okay");
        user.setFavourite("Okay");
        user.setProfilePicture("BlueFrog");

        given(userService.authUser(Mockito.anyLong(), Mockito.anyString())).willReturn(user);
        given(userService.editUser(any(), any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/{id}", 1L)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "OkayToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isOk());
    }

    @Test
    public void updateUser_Failure_WrongUser() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("Okay");
        userPutDTO.setFavourite("Okay");
        userPutDTO.setProfilePicture("BlueFrog");

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(userService).authUser(1L, "WrongToken");

        MockHttpServletRequestBuilder putRequest = put("/users/{id}", 1L)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "WrongToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());
    }
    @Test
    public void updateUser_Failure_NotFoundUser() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("Okay");
        userPutDTO.setFavourite("Okay");
        userPutDTO.setProfilePicture("BlueFrog");


        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(userService).authUser(1L, "WrongToken");

        MockHttpServletRequestBuilder putRequest = put("/users/{id}", 1L)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "WrongToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUser_inputValidation() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("base");
        user.setToken("OkayToken");

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("Okay");
        userPutDTO.setFavourite("Okay");
        userPutDTO.setProfilePicture("BlueFrog");

        given(userService.authUser(Mockito.anyLong(), Mockito.anyString())).willReturn(user);
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(userService).editUser(Mockito.any(), Mockito.any());


        MockHttpServletRequestBuilder putRequest = put("/users/{id}", 1L)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "OkayToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_Conflict() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("base");

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("Okay");
        userPutDTO.setFavourite("Okay");
        userPutDTO.setProfilePicture("BlueFrog");

        given(userService.authUser(Mockito.anyLong(), Mockito.anyString())).willReturn(user);
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT))
                .when(userService).editUser(Mockito.any(), Mockito.any());


        MockHttpServletRequestBuilder putRequest = put("/users/{id}", 1L)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "OkayToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isConflict());
    }

    @Test
    void getUserLobby_validInput_returnsLobby() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setToken("1");

        Lobby lobby = new Lobby(1234, "new lobby");
        Player player = new Player("123", "player", lobby);
        lobby.setPlayers(Collections.singletonList(player));
        user.setPlayer(player);
        player.setUser(user);

        given(userService.authUser(Mockito.anyLong(), Mockito.anyString())).willReturn(user);

        MockHttpServletRequestBuilder getRequest = get("/users/{id}/lobby", 1L)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("userToken", "1");

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(1234)))
                .andExpect(jsonPath("$.name", is("new lobby")));
    }

    @Test
    void getUserLobby_invalidInput_throwsException() throws Exception {
        given(userService.authUser(Mockito.anyLong(), Mockito.anyString())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        MockHttpServletRequestBuilder getRequest = get("/users/{id}/lobby", 1L)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("userToken", "1");

        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    void getUserLobby_userNotInLobby_throwsException() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setToken("1");

        given(userService.authUser(Mockito.anyLong(), Mockito.anyString())).willReturn(user);

        MockHttpServletRequestBuilder getRequest = get("/users/{id}/lobby", 1L)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("userToken", "1");

        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }

}