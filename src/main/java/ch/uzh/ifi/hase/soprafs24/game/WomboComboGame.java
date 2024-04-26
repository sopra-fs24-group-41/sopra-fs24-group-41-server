package ch.uzh.ifi.hase.soprafs24.game;

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

    public WomboComboGame(PlayerService playerService, CombinationService combinationService, WordService wordService) {
        super(playerService, combinationService, wordService);
        setup();
    }

    void setup() {
        super.setup();
    }

    public void setupPlayers(List<Player> players) {
        for (Player player : players) {
            player.setWords(startingWords);
            Word targetWord = wordService.getRandomWordWithinReachability(0.1, 0.3);
            player.setTargetWord(targetWord);
        }
    }

    public Word makeCombination(Player player, List<Word> words) {
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

        return result;
    }

    private void setNewTargetWord(Player player) {
        double minReachability = 0.1;
        Word targetWord = wordService.getRandomWordWithinReachability(minReachability, 0.3);
        int maxIter = 1000;
        int iter = 0;
        while (player.getWords().contains(targetWord)) {
            minReachability *= 0.75;
            targetWord = wordService.getRandomWordWithinReachability(minReachability, 0.3);
            iter += 1;
            if (iter >= maxIter) {
                player.setTargetWord(null);
                return;
            }
            if (iter >= maxIter / 2) {
                targetWord = combinationService.generateWordWithinReachability(minReachability, 0.3);
            }
        }
        player.setTargetWord(targetWord);
    }

    public boolean winConditionReached(Player player) {
        return player.getPoints() >= 50;
    }
}
