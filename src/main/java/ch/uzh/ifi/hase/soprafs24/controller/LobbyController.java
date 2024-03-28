package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerJoinedDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Lobby Controller
 * This class is responsible for handling all REST request that are related to lobbies.
 * The controller will receive the request and delegate the execution to the LobbyService and finally return the result.
 */
@RestController
public class LobbyController {

    private final LobbyService lobbyService;

    private final UserService userService;

    LobbyController(LobbyService lobbyService, UserService userService) {
        this.lobbyService = lobbyService;
        this.userService = userService;
    }

    @GetMapping("/lobbies")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyGetDTO> getAllLobbies() {
        List<Lobby> lobbies = lobbyService.getPublicLobbies();
        List<LobbyGetDTO> lobbyGetDTOS = new ArrayList<>();

        for (Lobby lobby : lobbies) {
            lobbyGetDTOS.add(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby));
        }
        return lobbyGetDTOS;
    }

    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerJoinedDTO createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
        if (lobbyPostDTO.getAnonymous() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only true or false value allowed for anonymous attribute");
        }

        if (!lobbyPostDTO.getAnonymous()) {
            User user = userService.checkToken(lobbyPostDTO.getUserToken());
            if (user.getPlayer() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "delete or leave your lobby before creating a new one");
            }
            Lobby lobby = lobbyService.createLobbyFromUser(user, lobbyPostDTO.getPublicAccess());
            return DTOMapper.INSTANCE.convertEntityToPlayerJoinedDTO(lobby.getOwner());
        } else if (lobbyPostDTO.getAnonymous()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "creating lobbies as anonymous user not supported");
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong in create lobby");
    }
}