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

import java.util.ArrayList;
import java.util.List;

public class Game {
    List<Word> startingWords;
    PlayerService playerService;
    WordService wordService;
    CombinationService combinationService;

    public Game(PlayerService playerService, CombinationService combinationService, WordService wordService) {
        this.playerService = playerService;
        this.combinationService = combinationService;
        this.wordService = wordService;
    }

    void setupStartingWords() {
        this.startingWords = new ArrayList<>();
        startingWords.add(wordService.getWord(new Word("air")));
        startingWords.add(wordService.getWord(new Word("earth")));
        startingWords.add(wordService.getWord(new Word("fire")));
        startingWords.add(wordService.getWord(new Word("water")));
    }

    public void setupPlayers(List<Player> players) {
        setupStartingWords();
        for (Player player : players) {
            playerService.resetPlayer(player);
            player.addWords(startingWords);
            player.setStatus(PlayerStatus.PLAYING);
        }
    }

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

        String errorMessage = "Standard game only allows combination of exactly two words!";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    public boolean winConditionReached(Player player) {
        return false;
    }
}