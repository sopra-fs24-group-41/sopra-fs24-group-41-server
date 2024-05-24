package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class WomboComboGame extends Game {
    private double minReachability = 0.0625;
    private double maxReachability = 0.075;

    public WomboComboGame(PlayerService playerService, CombinationService combinationService, WordService wordService) {
        super(playerService, combinationService, wordService);
    }

    @Override
    public void setupPlayers(List<Player> players) {
        setupStartingWords();
        for (Player player : players) {
            playerService.resetPlayer(player);
            player.addWords(startingWords);
            Word targetWord = wordService.selectTargetWord(minReachability, maxReachability);
            player.setTargetWord(targetWord);
            player.setStatus(PlayerStatus.PLAYING);
        }
    }

    @Override
    public Combination makeCombination(Player player, List<Word> words) {
        if (words.size() != 2) {
            String errorMessage = "Wombo Combo only allows combination of exactly two words!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        Combination combination = combinationService.getCombination(words.get(0), words.get(1));
        Word result = combination.getResult();

        if (!player.getWords().contains(result)) {
            player.addPoints(1);
            player.addWord(result);
        }

        if (player.getWords().contains(player.getTargetWord())) {
            player.addPoints(10);
            player.addWord(result);
            setNewTargetWord(player);
        }

        return combination;
    }

    void setNewTargetWord(Player player) {
        minReachability -= (double) (0.0125 * Math.floor(player.getPoints()/10.0f));
        maxReachability -= (double) (0.0125 * Math.floor(player.getPoints()/10.0f));
        Word targetWord = wordService.selectTargetWord(minReachability, maxReachability, player.getWords());
        player.setTargetWord(targetWord);
    }

    @Override
    public boolean winConditionReached(Player player) {
        return player.getPoints() >= 50;
    }
}
