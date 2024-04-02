package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();

        testUser2.setUsername("testUsername");
        testUser2.setPassword("anotherTestPassword");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    public void logInUser_validCredentials_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);

        User userCredentials = new User();
        userCredentials.setUsername(testUser.getUsername());
        userCredentials.setPassword(testUser.getPassword());

        User loggedInUser = userService.logInUser(userCredentials);
        assertEquals(testUser.getId(), loggedInUser.getId());
        assertEquals(testUser.getPassword(), loggedInUser.getPassword());
        assertEquals(testUser.getUsername(), loggedInUser.getUsername());
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
    }

    @Test
    public void logInUser_nonExistingUsername_throwsException() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);

        User userCredentials = new User();
        userCredentials.setUsername("non_existing_username");
        userCredentials.setPassword(testUser.getPassword());

        assertThrows(ResponseStatusException.class, () -> userService.logInUser(userCredentials));
    }

    @Test
    public void logInUser_wrongPassword_throwsException() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);

        User userCredentials = new User();
        userCredentials.setUsername(testUser.getUsername());
        userCredentials.setPassword("wrong password");

        assertThrows(ResponseStatusException.class, () -> userService.logInUser(userCredentials));
    }

    @Test
    public void logOutUser_validToken_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);
        userService.logInUser(testUser);

        User userCredentials = new User();
        userCredentials.setUsername(testUser.getUsername());
        userCredentials.setPassword(testUser.getPassword());

        String token = createdUser.getToken();

        userService.logOutUser(token);
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    public void logOutUser_nonExistingToken_throwsException() throws Exception {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);
        userService.logInUser(testUser);

        User userCredentials = new User();
        userCredentials.setUsername(testUser.getUsername());
        userCredentials.setPassword(testUser.getPassword());

        String token = createdUser.getToken() + "a";

        assertThrows(ResponseStatusException.class, () -> userService.logOutUser(token));
    }
}
