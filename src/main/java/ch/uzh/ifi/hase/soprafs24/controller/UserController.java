package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.achievements.Achievement;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AchievementService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;
    private final AchievementService achievementService;

    UserController(UserService userService, AchievementService achievementService) {
        this.userService = userService;
        this.achievementService = achievementService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserGetDTO getUser(@PathVariable String id) {
        long userIdLong = parseUserId(id);
        User foundUser = userService.getUserById(userIdLong);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(foundUser);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserGetDTO createUser(@RequestBody UserLoginPostDTO userLoginPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userLoginPostDTO);
        User createdUser = userService.createUser(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserGetDTO editUser(@PathVariable("id") String id,
                         @RequestBody UserPutDTO userPutDTO,
                         @RequestHeader(name = "Authorization") String token) {
        long userIdLong = parseUserId(id);
        userService.authUser(userIdLong, token);
        User update = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User updatedUser = userService.editUser(token, update);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
    }

    @PostMapping("/logins")
    @ResponseStatus(HttpStatus.OK)
    public UserSecretDTO logInUser(@RequestBody UserLoginPostDTO userLoginPostDTO) {
        User userCredentials = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userLoginPostDTO);
        User loggedInUser = userService.logInUser(userCredentials);
        return DTOMapper.INSTANCE.convertEntityToUserSecretGetDTO(loggedInUser);
    }

    @PostMapping("/logouts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void logOutUser(@RequestBody UserTokenPostDTO userTokenPostDTO) {
        User tokenUser = DTOMapper.INSTANCE.convertUserTokenPostDTOtoEntity(userTokenPostDTO);
        userService.logOutUser(tokenUser.getToken());
    }

    @GetMapping("/users/{id}/lobby")
    @ResponseStatus(HttpStatus.OK)
    public LobbyGetDTO getLobby(@PathVariable String id, @RequestHeader String userToken) {
        long userIdLong = parseUserId(id);
        User user = userService.authUser(userIdLong, userToken);
        if (user.getPlayer() == null || (user.getPlayer() != null && user.getPlayer().getLobby() == null)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not in a lobby");
        }
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(user.getPlayer().getLobby());
    }

    @DeleteMapping("users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id, @RequestHeader String userToken) {
        long userIdLong = parseUserId(id);
        User user = userService.authUser(userIdLong, userToken);
        userService.deleteUser(user);
    }

    private long parseUserId(String idString) {
        try {
            return Long.parseLong(idString);
        }
        catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Badly formatted lobby code. Full error message: " + e.getMessage());
        }
    }

    @GetMapping("/users/achievements")
    @ResponseStatus(HttpStatus.OK)
    public List<Achievement> getAchievements() {
        return achievementService.getAchievements();
    }
}
