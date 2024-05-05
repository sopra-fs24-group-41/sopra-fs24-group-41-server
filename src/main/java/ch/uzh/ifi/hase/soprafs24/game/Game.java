package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
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
        setup();
    }

    void setup() {
        this.startingWords = new ArrayList<>();
        startingWords.add(wordService.getWord(new Word("water")));
        startingWords.add(wordService.getWord(new Word("earth")));
        startingWords.add(wordService.getWord(new Word("fire")));
        startingWords.add(wordService.getWord(new Word("air")));
    }

    public void setupPlayers(List<Player> players) {
        for (Player player : players) {
            playerService.resetPlayer(player);
            player.addWords(startingWords);
        }
    }

    private void updatePlayerStatistics(Player player, Combination combination) {
        User user = player.getUser();
        Word resultWord = combination.getResult();
        if (user == null) {
            return;
        }

        user.setCombinationsMade(user.getCombinationsMade() + 1);
        if (wordService.checkUniqueWord(resultWord)) {
            user.setDiscoveredWords(user.getDiscoveredWords() + 1);
        }
        if (user.getRarestWordFound() == null || resultWord.getReachability() < user.getRarestWordFound().getReachability()) {
            user.setRarestWordFound(resultWord);
        }
    }

    public Word makeCombination(Player player, List<Word> words) {
        if (words.size() == 2) {
            Combination combination = combinationService.getCombination(words.get(0), words.get(1));
            player.addWord(combination.getResult());
            updatePlayerStatistics(player, combination);
            return combination.getResult();
        }

        String errorMessage = "Standard game only allows combination of exactly two words!";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    public boolean winConditionReached(Player player) {
        return false;
    }
}