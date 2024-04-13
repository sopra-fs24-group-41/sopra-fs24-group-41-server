package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

public class FusionFrenzyGame extends Game {
    private Word target;

    public FusionFrenzyGame(PlayerService playerService, CombinationService combinationService, WordService wordService) {
        super(playerService, combinationService, wordService);
        setup();
    }

    private void setup() {
        this.startingWords = new ArrayList<>();
        startingWords.add(wordService.getWord(new Word("water")));
        startingWords.add(wordService.getWord(new Word("earth")));
        startingWords.add(wordService.getWord(new Word("fire")));
        startingWords.add(wordService.getWord(new Word("air")));
    }

    public void setupPlayers(List<Player> players) {
        for (Player player : players) {
            player.setAvailableWords(startingWords);
            playerService.updatePlayer(player);
        }
    }

    public Player makeCombination(Player player, List<Word> words) {
        if (words.size() == 2) {
            Combination combination = combinationService.getCombination(words.get(0), words.get(1));
            player = playerService.addWordToPlayer(player, combination.getResult());
            return player;
        }

        String errorMessage = "Fusion Frenzy only allows combination of exactly two words!";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

    }

    public boolean winConditionReached(Player player) {
        return player.getAvailableWords().contains(target);
    }
}
