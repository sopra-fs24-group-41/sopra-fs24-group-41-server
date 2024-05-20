package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.PlayerWord;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class FiniteFusionGame extends Game {
    private static final Integer STARTING_USES = 10;

    public FiniteFusionGame(PlayerService playerService, CombinationService combinationService, WordService wordService) {
        super(playerService, combinationService, wordService);
    }

    @Override
    public void setupPlayers(List<Player> players) {
        setupStartingWords();
        Word targetWord = wordService.getRandomWordWithinReachability(0.1, 0.3);
        for (Player player : players) {
            playerService.resetPlayer(player);
            player.addWords(startingWords, STARTING_USES);
            player.setTargetWord(targetWord);
            player.setStatus(PlayerStatus.PLAYING);
        }
    }

    @Override
    public Word makeCombination(Player player, List<Word> words) {
        if (words.size() == 2) {
            if (player.getStatus() == PlayerStatus.PLAYING) {
                return playFiniteFusion(player, words);
            }

            if (player.getStatus() == PlayerStatus.LOST) {
                return playCasual(player, words);
            }
        }

        String errorMessage = "Finite Fusion only allows combination of exactly two words!";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    Word playFiniteFusion(Player player, List<Word> words) {
        PlayerWord playerWord1 = player.getPlayerWord(words.get(0));
        PlayerWord playerWord2 = player.getPlayerWord(words.get(1));
        if (playerWord1.getUses() > 0 && playerWord2.getUses() > 0) {
            playerWord1.addUses(-1);
            playerWord2.addUses(-1);
            Combination combination = combinationService.getCombination(words.get(0), words.get(1));
            Word result = combination.getResult();
            if (!player.getWords().contains(result)) {
                player.addPoints(1);
            }
            player.addWord(result, 1);
            if (player.getTotalUses() <= 1) {
                playerLoses(player);
            }
            return result;
        }
        String errorMessage = "No more uses of ingredient words left!";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    void playerLoses(Player player) {
        playerService.resetPlayer(player);
        setupStartingWords();
        player.addWords(startingWords);
        player.setTargetWord(null);
        player.setStatus(PlayerStatus.LOST);
    }

    private Word playCasual(Player player, List<Word> words) {
        Combination combination = combinationService.getCombination(words.get(0), words.get(1));
        player.addWord(combination.getResult());
        return combination.getResult();
    }

    @Override
    public boolean winConditionReached(Player player) {
        return player.getStatus() == PlayerStatus.PLAYING && player.getWords().contains(player.getTargetWord());
    }
}
