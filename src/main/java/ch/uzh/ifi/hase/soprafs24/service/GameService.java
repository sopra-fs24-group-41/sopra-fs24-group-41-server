package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.Instruction;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.game.FiniteFusionGame;
import ch.uzh.ifi.hase.soprafs24.game.WomboComboGame;
import ch.uzh.ifi.hase.soprafs24.websocket.TimeDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
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

    private static final String LOBBY_MESSAGE_DESTINATION_BASE = "/topic/lobbies";
    private static final String LOBBY_MESSAGE_DESTINATION_GAME= "/game";

    @Autowired
    public GameService(PlayerService playerService, CombinationService combinationService, WordService wordService, SimpMessagingTemplate messagingTemplate) {
        this.playerService = playerService;
        this.combinationService = combinationService;
        this.wordService = wordService;
        this.messagingTemplate = messagingTemplate;
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
            startGameTimer(lobby, new Timer());
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

    public Word play(Player player, List<Word> words) {
        Lobby lobby = player.getLobby();
        Game game = instantiateGame(lobby.getMode());
        Word result = game.makeCombination(player, words);

        if (game.winConditionReached(player)) {
            lobby.setStatus(LobbyStatus.PREGAME);
            messagingTemplate.convertAndSend(LOBBY_MESSAGE_DESTINATION_BASE + "/" + lobby.getCode() + LOBBY_MESSAGE_DESTINATION_GAME,
                    new InstructionDTO(Instruction.STOP));
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

    public void startGameTimer(Lobby lobby, Timer gameTimer) {

        TimerTask task = createGameTask(lobby, gameTimer);

        // Schedule the task to run every 10th second (like a while-loop but control over time, different thread used)
        // Use a three-second initial delay for the Client to receive the initial timer setup.
        gameTimer.scheduleAtFixedRate(task, 3000, 10000);
    }

    public TimerTask createGameTask(Lobby lobby, Timer gameTimer){
        return new TimerTask() {
            int remainingTime = lobby.getGameTime();

            public void run() {
                for(int t : new int[]{10, 30, 60, 180, 300})
                    if (remainingTime == t) {
                        messagingTemplate.convertAndSend(LOBBY_MESSAGE_DESTINATION_BASE + "/" + lobby.getCode() + LOBBY_MESSAGE_DESTINATION_GAME,
                                new TimeDTO(String.valueOf(t)));
                    }

                if (remainingTime <= 0) {
                    gameTimer.cancel(); // Stop the timer when time's up
                    lobby.setStatus(LobbyStatus.PREGAME);
                    messagingTemplate.convertAndSend(LOBBY_MESSAGE_DESTINATION_BASE + "/" + lobby.getCode() + LOBBY_MESSAGE_DESTINATION_GAME,
                            new InstructionDTO(Instruction.STOP));
                }
                remainingTime -= 10; // Decrement remaining time by 10
            }
        };
    }

}