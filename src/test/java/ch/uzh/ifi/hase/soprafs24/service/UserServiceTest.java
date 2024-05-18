package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private LobbyService lobbyService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        testUser.setToken("1234");
        testUser.setFavourite("Zaddy");
        testUser.setProfilePicture("BlueFrog");

        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    void createUser_duplicateName_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void logInUser_validInputs_returnsUser() {
        userService.createUser(testUser);
        User userCredentials = testUser;

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        User loggedInUser = userService.logInUser(userCredentials);
        assertEquals(testUser.getId(), loggedInUser.getId());
        assertEquals(testUser.getPassword(), loggedInUser.getPassword());
        assertEquals(testUser.getUsername(), loggedInUser.getUsername());
    }

    @Test
    void logInUser_validCredentials_setsOnlineStatus() {
        userService.createUser(testUser);
        User userCredentials = testUser;

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        User loggedInUser = userService.logInUser(userCredentials);
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
    }

    @Test
    void logInUser_nonExistingUsername_throwsException() {
        userService.createUser(testUser);
        User userCredentials = testUser;
        userCredentials.setUsername("non_existing_username");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.logInUser(userCredentials));
    }

    @Test
    void logInUser_wrongPassword_throwsException() {
        userService.createUser(testUser);
        User userCredentials = new User();
        userCredentials.setUsername(testUser.getUsername());
        userCredentials.setPassword("wrong_password");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.logInUser(userCredentials));
    }

    @Test
    void logOutUser_validToken_success() {
        testUser.setStatus(UserStatus.ONLINE);
        String token = testUser.getToken();

        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);
        userService.logOutUser(token);

        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
    }

    @Test
    void logOutUser_nonExistingToken_throwsException() {
        testUser.setStatus(UserStatus.ONLINE);
        String token = testUser.getToken() + "1234";

        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.logOutUser(token));
    }

    @Test
    void checkToken_validToken_success() {
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

        User checkedUser = userService.checkToken(testUser.getToken());
        assertEquals(testUser.getId(), checkedUser.getId());
        assertEquals(testUser.getPassword(), checkedUser.getPassword());
        assertEquals(testUser.getUsername(), checkedUser.getUsername());
    }

    @Test
    void checkToken_invalidToken_throwsUnauthorizedException() {
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.checkToken("1234"));
    }

    @Test
    void update_success() {
        // Mocking repository behavior
        User foundUser = new User();
        foundUser.setUsername("Peter");
        Mockito.when(userRepository.findByToken("1234")).thenReturn(foundUser);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        // Creating an updated user
        User updatedUser = new User();
        updatedUser.setProfilePicture("PinkBunny");
        updatedUser.setFavourite("Daddy");
        updatedUser.setUsername("Jason");

        // Invoking the method under test
        User checkUser = userService.editUser("1234", updatedUser);

        // Assertions
        assertEquals(checkUser.getFavourite(), updatedUser.getFavourite());
        assertEquals(checkUser.getUsername(), updatedUser.getUsername());
        assertEquals(checkUser.getProfilePicture(), updatedUser.getProfilePicture());
    }

    @Test
    void updateUser_usernameWithSpaces_badRequestException() {
        updateUser_badFormatting_badRequestException("Daddy", "Jas on");
    }

    @Test
    void updateUser_UserNameEmpty_badRequestException() {
        updateUser_badFormatting_badRequestException("Daddy", "");
    }

    @Test
    void updateUser_FavouriteWithSpaces_badRequestException() {
        updateUser_badFormatting_badRequestException("Da ddy", "Jason");
    }

    void updateUser_badFormatting_badRequestException(String favourite, String username) {
        User foundUser = new User();
        foundUser.setUsername("Peter");
        Mockito.when(userRepository.findByToken("1234")).thenReturn(foundUser);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        User updatedUser = new User();
        updatedUser.setProfilePicture("PinkBunny");
        updatedUser.setFavourite(favourite);
        updatedUser.setUsername(username);

        // Catching the ResponseStatusException
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.editUser("1234", updatedUser));

        // Verifying the status code
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void update_fail_conflict() {
        User foundUser = new User();
        foundUser.setUsername("Peter");
        User conflictUser = new User();
        conflictUser.setUsername("Jason");
        Mockito.when(userRepository.findByToken("1234")).thenReturn(foundUser);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(conflictUser);

        User updatedUser = new User();
        updatedUser.setProfilePicture("PinkBunny");
        updatedUser.setFavourite("Dadddy");
        updatedUser.setUsername("Jason");

        // Catching the ResponseStatusException
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.editUser("1234", updatedUser));

        // Verifying the status code
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void update_success_empty_favourite_to_zaddy() {
        // Mocking repository behavior
        User foundUser = new User();
        foundUser.setUsername("Peter");
        Mockito.when(userRepository.findByToken("1234")).thenReturn(foundUser);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        // Creating an updated user
        User updatedUser = new User();
        updatedUser.setProfilePicture("PinkBunny");
        updatedUser.setFavourite("");
        updatedUser.setUsername("Jason");

        // Invoking the method under test
        User checkUser = userService.editUser("1234", updatedUser);

        // Assertions
        assertEquals("Zaddy", checkUser.getFavourite());

    }

    @Test
    void update_success_unchanged_username() {
        // Mocking repository behavior
        User foundUser = new User();
        foundUser.setUsername("Peter");
        Mockito.when(userRepository.findByToken("1234")).thenReturn(foundUser);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        // Creating an updated user
        User updatedUser = new User();
        updatedUser.setProfilePicture("PinkBunny");
        updatedUser.setFavourite("Daddy");
        updatedUser.setUsername("Peter");

        // Invoking the method under test
        User checkUser = userService.editUser("1234", updatedUser);

        // Assertions
        assertEquals(checkUser.getFavourite(), updatedUser.getFavourite());
        assertEquals(checkUser.getUsername(), updatedUser.getUsername());
        assertEquals(checkUser.getProfilePicture(), updatedUser.getProfilePicture());
    }

    @Test
    void deleteUser_noPlayer_success() {
        // Mocking repository behavior
        Mockito.doNothing().when(userRepository).delete(Mockito.any());

        // Invoking the method under test
        userService.deleteUser(testUser);

        // Verifying the method call
        Mockito.verify(userRepository, Mockito.times(1)).delete(Mockito.any());
    }

    @Test
    void deleteUser_withPlayer_success() {
        // Mocking repository behavior
        Lobby lobby = new Lobby(1234, "TestLobby");

        Player player = new Player("123", "Peter", lobby);
        player.setLobby(lobby);
        player.setUser(testUser);
        testUser.setPlayer(player);

        Mockito.doNothing().when(userRepository).delete(Mockito.any());

        // Invoking the method under test
        userService.deleteUser(testUser);

        // Verifying the method call
        Mockito.verify(userRepository, Mockito.times(1)).delete(Mockito.any());
        Mockito.verify(playerService, Mockito.times(1)).removePlayer(player);
    }
}
