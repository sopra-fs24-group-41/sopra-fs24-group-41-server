package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.Instruction;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.websocket.InstructionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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

    private final GameService gameService;

    private final SimpMessagingTemplate messagingTemplate;
    private final CombinationService combinationService;
    private final APIService apiService;

    private static final String MESSAGE_LOBBY_BASE = "/topic/lobbies";
    private static final String MESSAGE_LOBBY_CODE = "/topic/lobbies/%d";
    private static final String MESSAGE_LOBBY_GAME = "/topic/lobbies/%d/game";

    LobbyController(LobbyService lobbyService, UserService userService, PlayerService playerService,
                    GameService gameService, SimpMessagingTemplate messagingTemplate, CombinationService combinationService, APIService apiService) {
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.playerService = playerService;
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
        this.combinationService = combinationService;
        this.apiService = apiService;
    }

    @GetMapping("/lobbies")
    @ResponseStatus(HttpStatus.OK)
    public List<LobbyGetDTO> getAllLobbies() {
        return getPublicLobbiesGetDTOList();
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
            messagingTemplate.convertAndSend(MESSAGE_LOBBY_BASE, new InstructionDTO(Instruction.UPDATE_LOBBY_LIST, getPublicLobbiesGetDTOList()));
            return DTOMapper.INSTANCE.convertEntityToPlayerJoinedDTO(player);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "creating lobby as anonymous user not supported, please supply userToken as header field");
        }
    }

    @PostMapping("/lobbies/{code}/players")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerJoinedDTO joinPlayer(@PathVariable String code, @RequestHeader(required = false) String userToken, @RequestBody(required = false) PlayerPostDTO playerPostDTO) {
        long lobbyCodeLong = parseLobbyCode(code);
        if (playerPostDTO == null) playerPostDTO = new PlayerPostDTO();

        Player player;
        if (userToken != null && !userToken.isEmpty()) {
            User user = userService.checkToken(userToken);
            player = lobbyService.joinLobbyFromUser(user, lobbyCodeLong);
        }
        else {
            player = lobbyService.joinLobbyAnonymous(playerPostDTO.getPlayerName(), lobbyCodeLong);
        }
        messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_CODE, lobbyCodeLong),
                new InstructionDTO(Instruction.UPDATE_LOBBY, DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(player.getLobby())));
        return DTOMapper.INSTANCE.convertEntityToPlayerJoinedDTO(player);
    }

    @GetMapping("/lobbies/{code}/players")
    @ResponseStatus(HttpStatus.OK)
    public List<PlayerGetDTO> getPlayers(@PathVariable String code) {
        long lobbyCodeLong = parseLobbyCode(code);
        Lobby lobby = lobbyService.getLobbyByCode(lobbyCodeLong);
        return lobby.getPlayers().stream().map(DTOMapper.INSTANCE::convertEntityToPlayerGetDTO).toList();
    }

    @PutMapping("/lobbies/{code}")
    @ResponseStatus(HttpStatus.OK)
    public LobbyGetDTO updateLobby(@PathVariable String code, @RequestBody LobbyPutDTO lobbyPutDTO, @RequestHeader String playerToken) {
        Lobby lobby = getAuthenticatedLobby(code, playerToken);

        lobby = lobbyService.updateLobby(lobby, lobbyPutDTO);
        Map<String, Boolean> updates = lobby.getUpdatedFields();
        if (updates.get("publicAccess") || updates.get("name")) {
            messagingTemplate.convertAndSend(MESSAGE_LOBBY_BASE, new InstructionDTO(Instruction.UPDATE_LOBBY_LIST, getPublicLobbiesGetDTOList()));
        }
        if (updates.containsValue(true)) {
            messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_CODE, lobby.getCode()),
                    new InstructionDTO(Instruction.UPDATE_LOBBY, DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby)));
        }

        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }

    @PostMapping("/lobbies/{code}/games")
    @ResponseStatus(HttpStatus.CREATED)
    public void startGame(@PathVariable String code, @RequestHeader String playerToken) {
        Lobby lobby = getAuthenticatedLobby(code, playerToken);
        if (lobby.getStatus() != LobbyStatus.PREGAME) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There is already an ongoing game in this lobby");
        }
        gameService.createNewGame(lobby);
        lobby.setStatus(LobbyStatus.INGAME);
        messagingTemplate.convertAndSend(MESSAGE_LOBBY_BASE, new InstructionDTO(Instruction.UPDATE_LOBBY_LIST, getPublicLobbiesGetDTOList()));
        messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobby.getCode()), new InstructionDTO(Instruction.START, null));
    }

    @DeleteMapping("/lobbies/{code}/games")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void stopGame(@PathVariable String code, @RequestHeader String playerToken) {
        Lobby lobby = getAuthenticatedLobby(code, playerToken);
        if (lobby.getStatus() != LobbyStatus.INGAME) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There is no ongoing game in this lobby");
        }
        gameService.endGame(lobby, "The game was stopped by the owner");
        messagingTemplate.convertAndSend(MESSAGE_LOBBY_BASE, new InstructionDTO(Instruction.UPDATE_LOBBY_LIST, getPublicLobbiesGetDTOList()));
        messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobby.getCode()), new InstructionDTO(Instruction.STOP, null));
    }

    @GetMapping("/lobbies/{lobbyCode}/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerGetDTO getPlayer(@PathVariable String lobbyCode, @PathVariable String playerId, @RequestHeader String playerToken) {
        Player player = getAuthenticatedPlayer(lobbyCode, playerId, playerToken);
        return DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);
    }

    @PutMapping("/lobbies/{lobbyCode}/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerPlayedDTO play(@PathVariable String lobbyCode, @PathVariable String playerId,
                                @RequestHeader String playerToken, @RequestBody List<Word> words) {
        long lobbyCodeLong = parseLobbyCode(lobbyCode);
        Player player = getAuthenticatedPlayer(lobbyCode, playerId, playerToken);
        Word result = gameService.play(player, words);
        messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobbyCodeLong),
                new InstructionDTO(Instruction.UPDATE_PLAYERS, player.getLobby().getPlayers().stream().map(DTOMapper.INSTANCE::convertEntityToPlayerGetDTO).toList()));

        PlayerPlayedDTO playerPlayedDTO = DTOMapper.INSTANCE.convertEntityToPlayerPlayedDTO(player);
        playerPlayedDTO.setResultWord(DTOMapper.INSTANCE.convertEntityToWordDTO(result));
        return playerPlayedDTO;
    }

    @DeleteMapping("/lobbies/{lobbyCode}/players/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePlayerFromLobby(@PathVariable String lobbyCode, @PathVariable String playerId, @RequestHeader String playerToken) {
        Player player = getAuthenticatedPlayer(lobbyCode, playerId, playerToken);
        long lobbyCodeLong = player.getLobby().getCode();
        if (player.getOwnedLobby() == null) {
            Lobby lobby = player.getLobby();
            playerService.removePlayer(player);
            messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_CODE, lobby.getCode()),
                    new InstructionDTO(Instruction.UPDATE_LOBBY, DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby)));
        }
        else {
            lobbyService.removeLobby(player.getOwnedLobby());
            messagingTemplate.convertAndSend(MESSAGE_LOBBY_BASE, new InstructionDTO(Instruction.UPDATE_LOBBY_LIST, getPublicLobbiesGetDTOList()));
            messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobbyCodeLong),
                    new InstructionDTO(Instruction.KICK, null, "The lobby was closed by the owner"));
        }
    }

    @GetMapping("/combinations/{word1}/{word2}")
    @ResponseStatus(HttpStatus.OK)
    public WordDTO getCombinationResult(@PathVariable String word1, @PathVariable String word2, @RequestHeader String method) {
        if (method.equalsIgnoreCase("default")) {
            Word result = combinationService.getCombination(new Word(word1), new Word(word2)).getResult();
            return DTOMapper.INSTANCE.convertEntityToWordDTO(result);
        }
        if (method.equalsIgnoreCase("vertex")){
            try {
                Word result = new Word(apiService.getVertexAIWord(word1, word2));
                return DTOMapper.INSTANCE.convertEntityToWordDTO(result);
            }
            catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong getting the result from VertexAI. Error message: " + e.getMessage());
            }
        }
        if (method.equalsIgnoreCase("random")){
            Word result = new Word(apiService.getRandomWord());
            return DTOMapper.INSTANCE.convertEntityToWordDTO(result);
        }
        if (method.equalsIgnoreCase("find")){
            try {
                Word result = combinationService.findCombination(new Word(word1), new Word(word2)).getResult();
                return DTOMapper.INSTANCE.convertEntityToWordDTO(result);
            }
            catch (CombinationNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid method: " + method + ". Available methods: default, vertex, random, find.");
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

    private Lobby getAuthenticatedLobby(String lobbyCode, String ownerToken) {
        long lobbyCodeLong = parseLobbyCode(lobbyCode);
        Lobby lobby = lobbyService.getLobbyByCode(lobbyCodeLong);

        if (ownerToken == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Include player token in your request header as playerToken");

        if (!Objects.equals(lobby.getOwner().getToken(), ownerToken)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Lobby does not belong to the player with the given token");

        return lobby;
    }

    private long parseLobbyCode(String codeString) {
        try {
            return Long.parseLong(codeString);
        }
        catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Badly formatted lobby code. Full error message: " + e.getMessage());
        }
    }

    private long parseId(String idString) {
        try {
            return Long.parseLong(idString);
        }
        catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Badly formatted id. Full error message: " + e.getMessage());
        }
    }

    private List<LobbyGetDTO> getPublicLobbiesGetDTOList() {
        List<Lobby> lobbies = lobbyService.getPublicLobbies();
        List<LobbyGetDTO> lobbyGetDTOS = new ArrayList<>();

        for (Lobby lobby : lobbies) {
            lobbyGetDTOS.add(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby));
        }
        return lobbyGetDTOS;
    }
}
