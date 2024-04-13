package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.game.FusionFrenzyGame;
import ch.uzh.ifi.hase.soprafs24.game.Game;
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

    @Autowired
    public GameService(PlayerService playerService, CombinationService combinationService, WordService wordService) {
        this.playerService = playerService;
        this.combinationService = combinationService;
        this.wordService = wordService;
        setupGameModes();
    }

    private void setupGameModes() {
        gameModes.put(GameMode.STANDARD, Game.class);
        gameModes.put(GameMode.FUSIONFRENZY, FusionFrenzyGame.class);
    }

    public void createNewGame(Lobby lobby) {
        Game game = instantiateGame(lobby.getMode());
        game.setupPlayers(lobby.getPlayers());
    }

    public void play(Player player, List<Word> words) {
        Lobby lobby = player.getLobby();
        Game game = instantiateGame(lobby.getMode());
        player = game.makeCombination(player, words);

        if (game.winConditionReached(player)) {
            playerService.addPointsToPlayer(player, 1L);  // notify that player has won or something
        }

        // notify stuff
    }

    private Game instantiateGame(GameMode gameMode) {
        Class<? extends Game> gameClass = gameModes.get(gameMode);
        Class[] parameterTypes = {CombinationService.class, WordService.class, PlayerService.class};
        try {
            return gameClass.getDeclaredConstructor(parameterTypes).newInstance(combinationService, wordService, playerService);
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            String errorMessage = String.format("Game mode %s could not be instantiated!", gameMode.name());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }
    }
}
