package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerJoinedDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
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

    private final PlayerService playerService;

    LobbyController(LobbyService lobbyService, UserService userService, PlayerService playerService) {
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.playerService = playerService;
    }

    @GetMapping("/lobbies")
    @ResponseStatus(HttpStatus.OK)
    public List<LobbyGetDTO> getAllLobbies() {
        List<Lobby> lobbies = lobbyService.getPublicLobbies();
        List<LobbyGetDTO> lobbyGetDTOS = new ArrayList<>();

        for (Lobby lobby : lobbies) {
            lobbyGetDTOS.add(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby));
        }
        return lobbyGetDTOS;
    }

    @GetMapping("/lobbies/{code}")
    public LobbyGetDTO getLobbyByCode(@PathVariable String code) {
        long parsedLobbyCode = parseLobbyCode(code);
        Lobby lobby = lobbyService.getLobbyByCode(parsedLobbyCode);
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }

    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerJoinedDTO createLobby(@RequestBody LobbyPostDTO lobbyPostDTO, @RequestHeader String userToken) {
        if (userToken != null && !userToken.isEmpty()) {
            User user = userService.checkToken(userToken);
            if (user.getPlayer() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Your user already has a lobby associated, leave it before creating a new one.");
            }
            Player player = lobbyService.createLobbyFromUser(user, lobbyPostDTO.getPublicAccess());
            return DTOMapper.INSTANCE.convertEntityToPlayerJoinedDTO(player);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "creating lobby as anonymous user not supported, please supply userToken as header field");
        }
    }

    @PostMapping("/lobbies/{code}/players")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerJoinedDTO joinPlayer(@PathVariable String code, @RequestHeader String userToken) {
        long lobbyCodeLong = parseLobbyCode(code);

        if (userToken != null && !userToken.isEmpty()) {
            User user = userService.checkToken(userToken);
            if (user.getPlayer() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Your user already has a lobby associated, leave it before joining a new one.");
            }
            Player player = lobbyService.joinLobbyFromUser(user, lobbyCodeLong);
            return DTOMapper.INSTANCE.convertEntityToPlayerJoinedDTO(player);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "joining lobby as anonymous user not supported, please supply userToken as header field");
        }
    }

    @DeleteMapping("/lobbies/{lobbyCode}/players/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePlayerFromLobby(@PathVariable String lobbyCode, @PathVariable String playerId, @RequestHeader String playerToken) {
        // check inputs
        if (playerToken == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "include player token in your request header as playerToken");
        }
        long lobbyCodeLong = parseLobbyCode(lobbyCode);
        long playerIdLong = parseId(playerId);

        // get and check player
        Player player = playerService.checkToken(playerToken);
        if (player.getId() != playerIdLong) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Your player has id %d, but you tried to remove player with id %d", player.getId(), playerIdLong));
        }
        else if (player.getLobby().getCode() != lobbyCodeLong) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong lobby code for the player you tried to delete");
        }

        // remove player
        if (player.getOwnedLobby() == null) {
            playerService.removePlayer(player);
        } else {
            lobbyService.removeLobby(player.getOwnedLobby());
        }
    }

    private long parseLobbyCode(String codeString) {
        try {
            return Long.parseLong(codeString);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Badly formatted lobby code");
        }
    }

    private long parseId(String idString) {
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Badly formatted id");
        }
    }
}
