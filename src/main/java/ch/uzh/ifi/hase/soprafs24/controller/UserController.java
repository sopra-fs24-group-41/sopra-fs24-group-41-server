package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserSecretDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTokenPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
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

    @GetMapping("/users/{username}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable String username) {
        User foundUser = userService.getUserByUsername(username);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(foundUser);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserLoginPostDTO userLoginPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userLoginPostDTO);
        User createdUser = userService.createUser(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @PostMapping("/logins")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserSecretDTO logInUser(@RequestBody UserLoginPostDTO userLoginPostDTO) {
        User userCredentials = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userLoginPostDTO);
        User loggedInUser = userService.logInUser(userCredentials);
        return DTOMapper.INSTANCE.convertEntityToUserSecretGetDTO(loggedInUser);
    }

    @PostMapping("/logouts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    void logOutUser(@RequestBody UserTokenPostDTO userTokenPostDTO) {
        User tokenUser = DTOMapper.INSTANCE.convertUserTokenPostDTOtoEntity(userTokenPostDTO);
        userService.logOutUser(tokenUser.getToken());
    }
}
