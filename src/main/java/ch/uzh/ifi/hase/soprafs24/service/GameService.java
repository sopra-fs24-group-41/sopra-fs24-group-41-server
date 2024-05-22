package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.Instruction;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.game.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.websocket.TimeDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.soprafs24.websocket.InstructionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final PlayerService playerService;
    private final CombinationService combinationService;
    private final WordService wordService;
    private final EnumMap<GameMode, Class<? extends Game>> gameModes = new EnumMap<>(GameMode.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final PlatformTransactionManager transactionManager;
    private final LobbyService lobbyService;

    private final DailyChallengeService dailyChallengeService;

    private final Map<Long, Timer> timers;
    private static final String MESSAGE_LOBBY_BASE = "/topic/lobbies";
    private static final String MESSAGE_LOBBY_GAME = "/topic/lobbies/%d/game";
    private final AchievementService achievementService;

    @Autowired
    public GameService(PlayerService playerService, CombinationService combinationService, WordService wordService,
                       SimpMessagingTemplate messagingTemplate, PlatformTransactionManager transactionManager,
                       LobbyService lobbyService, DailyChallengeService dailyChallengeService,
                       AchievementService achievementService) {
        this.playerService = playerService;
        this.combinationService = combinationService;
        this.wordService = wordService;
        this.messagingTemplate = messagingTemplate;
        this.timers = new HashMap<>();
        this.transactionManager = transactionManager;
        this.lobbyService = lobbyService;
        this.dailyChallengeService = dailyChallengeService;
        this.achievementService = achievementService;
        setupGameModes();
    }

    private void setupGameModes() {
        gameModes.put(GameMode.STANDARD, Game.class);
        gameModes.put(GameMode.FUSIONFRENZY, FusionFrenzyGame.class);
        gameModes.put(GameMode.WOMBOCOMBO, WomboComboGame.class);
        gameModes.put(GameMode.FINITEFUSION, FiniteFusionGame.class);
        gameModes.put(GameMode.DAILYCHALLENGE, DailyChallengeGame.class);
    }

    public void createNewGame(Lobby lobby) {
        lobby.setStartTime(LocalDateTime.now());
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

    void updateWinsAndLosses(Lobby lobby) {
        for (Player player : lobby.getPlayers()) {
            if (player.getStatus() == PlayerStatus.WON) {
                player.addWinsToUser(1);
            }
            else if (player.getStatus() == PlayerStatus.LOST) {
                player.addLossesToUser(1);
            }
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

        if (user.getRarestWordFound() == null || (result.getReachability() != null && result.getReachability() < user.getRarestWordFound().getReachability())) {
            user.setRarestWordFound(result);
        }
    }

    public Word play(Player player, List<Word> words) {
        Lobby lobby = player.getLobby();
        Game game = instantiateGame(lobby.getMode());
        Combination combination = game.makeCombination(player, words);
        updatePlayerStatistics(player, combination.getResult());

        if (game.winConditionReached(player)) {
            player = playerService.setWinnerAndLoser(player);
            endGame(lobby, String.format("%s has won the game!", player.getName()));
        }
        else if (allPlayersLost(lobby)) {
            endGame(lobby, "All players have lost the game!");
        }

        achievementService.awardAchievements(player, combination);

        return combination.getResult();
    }

    private Game instantiateGame(GameMode gameMode) {
        Class<? extends Game> gameClass = gameModes.get(gameMode);
        Class[] parameterTypes = {PlayerService.class, CombinationService.class, WordService.class};
        try {
            if (gameMode != GameMode.DAILYCHALLENGE) {
                return gameClass.getDeclaredConstructor(parameterTypes).newInstance(playerService, combinationService, wordService);
            }
            parameterTypes = new Class[]{PlayerService.class, CombinationService.class, WordService.class, DailyChallengeService.class};
            return gameClass.getDeclaredConstructor(parameterTypes).newInstance(playerService, combinationService, wordService, dailyChallengeService);
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            String errorMessage = String.format("Game mode %s could not be instantiated! Exception: %s", gameMode.name(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }
    }

    public void endGame(Lobby lobby, String reason) {
        cancelAndRemoveTimer(lobby.getCode());

        lobby.setStatus(LobbyStatus.PREGAME);
        lobby.setGameTime(0);

        updateWinsAndLosses(lobby);
        if (lobby.getMode() == GameMode.DAILYCHALLENGE)
            dailyChallengeService.updateRecords(lobby);

        messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobby.getCode()), new InstructionDTO(Instruction.STOP, null, reason));
        messagingTemplate.convertAndSend(MESSAGE_LOBBY_BASE,
                new InstructionDTO(Instruction.UPDATE_LOBBY_LIST, lobbyService.getPublicLobbies().stream().map(DTOMapper.INSTANCE::convertEntityToLobbyGetDTO).toList()));
    }

    public void startTimer(Lobby lobby){
        Timer gameTimer = timers.get(lobby.getCode());
        TimerTask task = createGameTask(lobby);
        // Schedule the task to run every 10th second (like a while-loop but control over time, different thread used)
        // Use a three-second initial delay for the Client to receive the initial timer setup.
        gameTimer.scheduleAtFixedRate(task, 3000, 10000);
    }

    public TimerTask createGameTask(Lobby lobby){
        return new TimerTask() {
            int remainingTime = lobby.getGameTime();
            final long lobbyCode = lobby.getCode();

            public void run() {
                try {
                    if (remainingTime <= 0) {
                        endTimer(lobbyCode);
                    }

                    for (int t : new int[]{10, 30, 60, 180, 300}) {
                        if (remainingTime == t) {
                            messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobbyCode),
                                    new InstructionDTO(Instruction.UPDATE_TIMER, new TimeDTO(String.valueOf(t))));
                            break;
                        }
                    }

                    remainingTime -= 10; // Decrement remaining time by 10
                } catch (Exception e) {
                    log.error("Error in game timer task: ", e);
                }
            }
        };
    }

    private void endTimer(long lobbyCode) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            Lobby lobby = lobbyService.getLobbyByCode(lobbyCode);
            if (lobby == null) {
                cancelAndRemoveTimer(lobbyCode);
                return null;
            }
            if (lobby.getMode() != GameMode.STANDARD) {
                setPlayersLost(lobby);
            }
            endGame(lobby, "Time is up!");
            return null;
        });
    }

    private void setPlayersLost(Lobby lobby) {
        for (Player player : lobby.getPlayers()) {
            if (player.getStatus() == PlayerStatus.PLAYING) {
                player.setStatus(PlayerStatus.LOST);
            }
        }
    }

    private void cancelAndRemoveTimer(long lobbyCode) {
        Timer gameTimer = timers.get(lobbyCode);
        if (gameTimer != null) {
            gameTimer.cancel();
            timers.remove(lobbyCode);
        }
    }

    public boolean allPlayersLost(Lobby lobby) {
        return lobby.getPlayers().stream().allMatch(player -> player.getStatus() == PlayerStatus.LOST);
    }
}