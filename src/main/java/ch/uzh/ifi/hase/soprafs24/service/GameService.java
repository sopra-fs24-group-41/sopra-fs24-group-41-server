package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.Instruction;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
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

    private Timer gameTime;



    @Autowired
    public GameService(PlayerService playerService, CombinationService combinationService, WordService wordService, SimpMessagingTemplate messagingTemplate) {
        this.playerService = playerService;
        this.combinationService = combinationService;
        this.wordService = wordService;
        this.messagingTemplate = messagingTemplate;
        this.gameTime = new Timer();
        setupGameModes();
    }

    private void setupGameModes() {
        gameModes.put(GameMode.STANDARD, Game.class);
        gameModes.put(GameMode.FUSIONFRENZY, FusionFrenzyGame.class);
        gameModes.put(GameMode.WOMBOCOMBO, WomboComboGame.class);
    }

    public void createNewGame(Lobby lobby) {
        gameTimeRefresh(); //So Java is happy and restarting games work.
        startGameTimer(lobby);

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
            if(lobby.getMode().equals(GameMode.FUSIONFRENZY)){
                gameTime.cancel();
            }
            messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getCode() + "/game", new InstructionDTO(Instruction.stop));
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

    public void gameTimeRefresh(){
        //This part is needed, else Java complaints because of gameTime being cancelled and doesn't allow to restart new games.
        if (this.gameTime != null) {
            this.gameTime.cancel();
        }
        this.gameTime = new Timer();
    }
    public void startGameTimer(Lobby lobby) {
        System.out.println("New Timer, New Game");
        TimerTask task = new TimerTask() {
            int remainingTime = 60;

            @Override
            public void run() {
                if (remainingTime ==  30) {
                    System.out.println("You have 30sec left");
                    messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getCode() + "/game", new TimeDTO("30"));
                } else if (remainingTime == 10){
                    System.out.println("You have 10sec left");
                    messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getCode() + "/game", new TimeDTO("10"));

                } else if (remainingTime == 0) {
                    System.out.println("Time's up!");
                    gameTime.cancel(); // Stop the timer when time's up
                    lobby.setStatus(LobbyStatus.PREGAME);
                    messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getCode() + "/game", new InstructionDTO(Instruction.timeout));
                    messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getCode() + "/game", new InstructionDTO(Instruction.stop));
                }
                remainingTime--; // Decrement remaining time
            }
        };
        // Schedule the task to run every second
        gameTime.scheduleAtFixedRate(task, 0, 1000);
    }
}