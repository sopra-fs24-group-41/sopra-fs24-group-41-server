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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
class UserServiceIntegrationTest {

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
    void createUser_validInputs_success() {
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
    void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();

        testUser2.setUsername("testUsername");
        testUser2.setPassword("anotherTestPassword");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    void logInUser_validCredentials_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

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
    void logInUser_nonExistingUsername_throwsException() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User userCredentials = new User();
        userCredentials.setUsername("non_existing_username");
        userCredentials.setPassword(testUser.getPassword());

        assertThrows(ResponseStatusException.class, () -> userService.logInUser(userCredentials));
    }

    @Test
    void logInUser_wrongPassword_throwsException() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User userCredentials = new User();
        userCredentials.setUsername(testUser.getUsername());
        userCredentials.setPassword("wrong password");

        assertThrows(ResponseStatusException.class, () -> userService.logInUser(userCredentials));
    }

    @Test
    void logOutUser_validToken_success() {
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
    void logOutUser_nonExistingToken_throwsException() {
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

    @Test
    void checkToken_validToken_success() {
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1234");
        testUser.setCreationDate(LocalDate.now());
        userRepository.save(testUser);

        User checkedUser = userService.checkToken(testUser.getToken());
        assertEquals(testUser.getPassword(), checkedUser.getPassword());
        assertEquals(testUser.getUsername(), checkedUser.getUsername());
        assertEquals(testUser.getStatus(), checkedUser.getStatus());
        assertEquals(testUser.getToken(), checkedUser.getToken());
    }

    @Test
    void checkToken_invalidToken_throwsUnauthorizedException() {
        User testUser = new User();
        testUser.setId(3L);
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1234");
        testUser.setCreationDate(LocalDate.now());
        userRepository.saveAndFlush(testUser);

        String faultyToken = testUser.getToken() + "2";
        assertThrows(ResponseStatusException.class, () -> userService.checkToken(faultyToken));
    }
}
