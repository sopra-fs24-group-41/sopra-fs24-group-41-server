package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testName");
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getName(), createdUser.getName());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateName_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void createUser_duplicateInputs_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void logInUser_validInputs_returnsUser() {
        userService.createUser(testUser);
        User userCredentials = testUser;

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        User loggedInUser = userService.logInUser(userCredentials);
        assertEquals(testUser.getId(), loggedInUser.getId());
        assertEquals(testUser.getName(), loggedInUser.getName());
        assertEquals(testUser.getPassword(), loggedInUser.getPassword());
        assertEquals(testUser.getUsername(), loggedInUser.getUsername());
    }

    @Test
    public void logInUser_validCredentials_setsOnlineStatus() {
        userService.createUser(testUser);
        User userCredentials = testUser;

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        User loggedInUser = userService.logInUser(userCredentials);
        assertEquals(loggedInUser.getStatus(), UserStatus.ONLINE);
    }

    @Test
    public void logInUser_nonExistingUsername_throwsException() {
        userService.createUser(testUser);
        User userCredentials = testUser;
        userCredentials.setUsername("non_existing_username");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.logInUser(userCredentials));
    }

    @Test
    public void logInUser_wrongPassword_throwsException() {
        userService.createUser(testUser);
        User userCredentials = new User();
        userCredentials.setUsername(testUser.getUsername());
        userCredentials.setPassword("wrong_password");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.logInUser(userCredentials));
    }
}
