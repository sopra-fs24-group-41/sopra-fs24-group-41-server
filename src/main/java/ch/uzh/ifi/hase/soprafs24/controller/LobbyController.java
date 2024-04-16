package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
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
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Your user already has a lobby associated, leave it before creating a new one.");
            }
            Player player = lobbyService.createLobbyFromUser(user, lobbyPostDTO.getPublicAccess());
            return DTOMapper.INSTANCE.convertEntityToPlayerJoinedDTO(player);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "creating lobby as anonymous user not supported, please supply userToken as header field");
        }
    }

    @PostMapping("/lobbies/{code}/players")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerJoinedDTO joinPlayer(@PathVariable String code, @RequestHeader String userToken) {
        long lobbyCodeLong = parseLobbyCode(code);

        if (userToken != null && !userToken.isEmpty()) {
            User user = userService.checkToken(userToken);
            if (user.getPlayer() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Your user already has a lobby associated, leave it before joining a new one.");
            }
            Player player = lobbyService.joinLobbyFromUser(user, lobbyCodeLong);
            return DTOMapper.INSTANCE.convertEntityToPlayerJoinedDTO(player);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "joining lobby as anonymous user not supported, please supply userToken as header field");
        }
    }

    @PutMapping("/lobbies/{lobbyCode}/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public void play(@PathVariable String lobbyCode, @PathVariable String playerId, @RequestHeader String playerToken) {
        Player player = getAuthenticatedPlayer(lobbyCode, playerId, playerToken);
    }

    @DeleteMapping("/lobbies/{lobbyCode}/players/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePlayerFromLobby(@PathVariable String lobbyCode, @PathVariable String playerId, @RequestHeader String playerToken) {
        Player player = getAuthenticatedPlayer(lobbyCode, playerId, playerToken);
        if (player.getOwnedLobby() == null) playerService.removePlayer(player);
        else lobbyService.removeLobby(player.getOwnedLobby());
    }

    private Player getAuthenticatedPlayer(String lobbyCode, String playerId, String playerToken) {
        long lobbyCodeLong = parseLobbyCode(lobbyCode);
        long playerIdLong = parseId(playerId);

        if (playerToken == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Include player token in your request header as playerToken");

        Player player = playerService.findPlayerByToken(playerToken);

        if (player.getLobby().getCode() != lobbyCodeLong) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Player is not in lobby with code %s", lobbyCodeLong));

        if (player.getId() != playerIdLong) throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                String.format("Wrong token for player with ID %s", playerIdLong));

        return player;
    }

    private long parseLobbyCode(String codeString) {
        try {
            return Long.parseLong(codeString);
        }
        catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Badly formatted lobby code");
        }
    }

    private long parseId(String idString) {
        try {
            return Long.parseLong(idString);
        }
        catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Badly formatted id");
        }
    }
}
