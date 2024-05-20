package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.DailyChallenge;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.DailyChallengeService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class DailyChallengeGame extends Game {

    DailyChallengeService dailyChallengeService;
    public DailyChallengeGame(PlayerService playerService, CombinationService combinationService, WordService wordService, DailyChallengeService dailyChallengeService) {
        super(playerService, combinationService, wordService);
        this.dailyChallengeService = dailyChallengeService;
    }

    @Override
    public void setupPlayers(List<Player> players) {
        setupStartingWords();
        Word targetWord = dailyChallengeService.getTargetWord();
        for (Player player : players) {
            playerService.resetPlayer(player);
            player.addWords(startingWords);
            player.setTargetWord(targetWord);
            player.setStatus(PlayerStatus.PLAYING);
        }
    }

    @Override
    public Word makeCombination(Player player, List<Word> words) {
        if (words.size() == 2) {
            Combination combination = combinationService.getCombination(words.get(0), words.get(1));
            Word result = combination.getResult();
            player.addPoints(1);
            if (!player.getWords().contains(result)) {
                player.addWord(result);
            }
            return combination.getResult();
        }

        String errorMessage = "Daily challenge only allows combination of exactly two words!";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @Override
    public boolean winConditionReached(Player player) {
        player.setStatus(PlayerStatus.WON);
        return player.getWords().contains(player.getTargetWord());
    }
}
