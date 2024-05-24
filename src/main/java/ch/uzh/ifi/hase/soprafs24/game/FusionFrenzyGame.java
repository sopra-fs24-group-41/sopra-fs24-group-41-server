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

public class FusionFrenzyGame extends Game {
    private final double minReachability = 0.0125;
    private final double maxReachability = 0.0625;

    public FusionFrenzyGame(PlayerService playerService, CombinationService combinationService, WordService wordService) {
        super(playerService, combinationService, wordService);
    }

    @Override
    public void setupPlayers(List<Player> players) {
        setupStartingWords();
        Word targetWord = wordService.selectTargetWord(minReachability, maxReachability);
        for (Player player : players) {
            playerService.resetPlayer(player);
            player.addWords(startingWords);
            player.setTargetWord(targetWord);
            player.setStatus(PlayerStatus.PLAYING);
        }
    }

    @Override
    public Combination makeCombination(Player player, List<Word> words) {
        if (words.size() == 2) {
            Combination combination = combinationService.getCombination(words.get(0), words.get(1));
            Word result = combination.getResult();
            if (!player.getWords().contains(result)) {
                player.addPoints(1);
                player.addWord(result);
            }
            return combination;
        }

        String errorMessage = "Fusion Frenzy only allows combination of exactly two words!";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @Override
    public boolean winConditionReached(Player player) {
        return player.getWords().contains(player.getTargetWord());
    }
}
