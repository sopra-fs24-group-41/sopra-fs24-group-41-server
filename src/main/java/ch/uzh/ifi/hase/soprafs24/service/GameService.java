package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.Instruction;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.game.FiniteFusionGame;
import ch.uzh.ifi.hase.soprafs24.game.WomboComboGame;
import ch.uzh.ifi.hase.soprafs24.websocket.TimeDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.soprafs24.game.FusionFrenzyGame;
import ch.uzh.ifi.hase.soprafs24.game.Game;
import ch.uzh.ifi.hase.soprafs24.websocket.InstructionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


@Service
@Transactional
public class GameService {
    private final PlayerService playerService;
    private final CombinationService combinationService;
    private final WordService wordService;
    private final EnumMap<GameMode, Class<? extends Game>> gameModes = new EnumMap<>(GameMode.class);
    private final SimpMessagingTemplate messagingTemplate;

    private static final String MESSAGE_LOBBY_GAME = "/topic/lobbies/%d/game";
    private final LobbyService lobbyService;

    private final Map<Long, Timer> timers;


    @Autowired
    public GameService(PlayerService playerService, CombinationService combinationService, WordService wordService, LobbyService lobbyService, SimpMessagingTemplate messagingTemplate) {
        this.playerService = playerService;
        this.combinationService = combinationService;
        this.wordService = wordService;
        this.messagingTemplate = messagingTemplate;
        this.lobbyService = lobbyService;
        this.timers = new HashMap<>();

        setupGameModes();
    }

    private void setupGameModes() {
        gameModes.put(GameMode.STANDARD, Game.class);
        gameModes.put(GameMode.FUSIONFRENZY, FusionFrenzyGame.class);
        gameModes.put(GameMode.WOMBOCOMBO, WomboComboGame.class);
        gameModes.put(GameMode.FINITEFUSION, FiniteFusionGame.class);
    }
    public void createNewGame(Lobby lobby) {
        if(lobby.getGameTime() > 0){
            timers.put(lobby.getCode(), new Timer());
            startTimer(lobby);
        }

        List<Player> players = lobby.getPlayers();
        if (players != null && !players.isEmpty()) {
            Game game = instantiateGame(lobby.getMode());
            game.setupPlayers(players);
            return;
        }

        String errorMessage = String.format("There are no players in the lobby %s!", lobby.getName());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    void updateWinsAndLosses(Player winner, Lobby lobby) {
        for (Player player : lobby.getPlayers()) {
            if (player == winner)
                player.addWinsToUser(1);
            else
                player.addLossesToUser(1);
        }
    }

    void updatePlayerStatistics(Player player, Word result) {
        User user = player.getUser();
        if (user == null) {
            return;
        }

        user.setCombinationsMade(user.getCombinationsMade() + 1);
        if (result.isNewlyDiscovered()) {
            user.setDiscoveredWords(user.getDiscoveredWords() + 1);
        }
        if (user.getRarestWordFound() == null || result.getReachability() < user.getRarestWordFound().getReachability()) {
            user.setRarestWordFound(result);
        }
    }

    public Word play(Player player, List<Word> words) {
        Lobby lobby = player.getLobby();
        Game game = instantiateGame(lobby.getMode());
        Word result = game.makeCombination(player, words);
        updatePlayerStatistics(player, result);

        if (game.winConditionReached(player)) {
            if(timers.get(lobby.getCode()) != null){
                timers.get(lobby.getCode()).cancel();
                timers.remove(lobby.getCode());
            }

            lobby.setStatus(LobbyStatus.PREGAME);
            lobby.setGameTime(0);
            messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getCode() + "/game", new InstructionDTO(Instruction.stop));
            updateWinsAndLosses(player, lobby);
            messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobby.getCode()), new InstructionDTO(Instruction.STOP));
        }

        return result;
    }

    private Game instantiateGame(GameMode gameMode) {
        Class<? extends Game> gameClass = gameModes.get(gameMode);
        Class[] parameterTypes = {PlayerService.class, CombinationService.class, WordService.class};
        try {
            return gameClass.getDeclaredConstructor(parameterTypes).newInstance(playerService, combinationService, wordService);
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            String errorMessage = String.format("Game mode %s could not be instantiated! Exception: %s", gameMode.name(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }
    }


    public void startTimer(Lobby lobby){
        Timer gameTimer = timers.get(lobby.getCode());
        TimerTask task = createGameTask(lobby, gameTimer);
        // Schedule the task to run every 10th second (like a while-loop but control over time, different thread used)
        // Use a three-second initial delay for the Client to receive the initial timer setup.
        gameTimer.scheduleAtFixedRate(task, 3000, 10000);
    }


    public TimerTask createGameTask(Lobby lobby, Timer gameTimer){
        return new TimerTask() {

            int remainingTime = lobby.getGameTime();

            public void run() {
                if (remainingTime <= 0) {
                    gameTimer.cancel(); // Stop the timer when time's up
                    timers.remove(lobby.getCode());
                    lobbyService.setStatusGivenLobby(lobby, LobbyStatus.PREGAME);
                    messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getCode() + "/game", new InstructionDTO(Instruction.stop));
                }

                for(int t : new int[]{10, 30, 60, 180, 300})
                    if (remainingTime == t) {
                        messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobby.getCode()),
                                new TimeDTO(String.valueOf(t)));
                    }

                if (remainingTime <= 0) {
                    gameTimer.cancel(); // Stop the timer when time's up
                    lobby.setStatus(LobbyStatus.PREGAME);
                    messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobby.getCode()),
                            new InstructionDTO(Instruction.STOP));
                }
                remainingTime -= 10; // Decrement remaining time by 10
            }
        };
    }

}