package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
    public User getUserByUsername(String username) {
        User foundUser = userRepository.findByUsername(username);
        if (foundUser == null) {
            String errorMessage = "Cannot find this username.";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
        return foundUser;
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setProfilePicture("");  // for now, the profile picture is empty
        checkDuplicateUser(newUser);
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User logInUser(User userCredentials) {
        User userByUsername = userRepository.findByUsername(userCredentials.getUsername());

        if (userByUsername == null) {
            String errorMessage = "Cannot find this username.";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
        if (!Objects.equals(userCredentials.getPassword(), userByUsername.getPassword())) {
            String errorMessage = "Wrong password";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }

        userByUsername.setStatus(UserStatus.ONLINE);
        return userByUsername;
    }

    public void logOutUser(String userToken) {
        User userByToken = userRepository.findByToken(userToken);

        if (userByToken == null) {
            String errorMessage = "Logging out unsuccessful: Unknown user (invalid token).";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }

        userByToken.setStatus(UserStatus.OFFLINE);
    }

    public User checkToken(String userToken) {
        User userByToken = userRepository.findByToken(userToken);

        if (userByToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user token has insufficient access rights");
        }
        return userByToken;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkDuplicateUser(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String errorMessage = "The username provided is not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }
}
