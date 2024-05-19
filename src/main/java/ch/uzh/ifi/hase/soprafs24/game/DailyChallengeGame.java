package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;

public class DailyChallengeGame extends Game {

    private Word targetWord;
    public DailyChallengeGame(PlayerService playerService, CombinationService combinationService, WordService wordService, Word targetWord) {
        super(playerService, combinationService, wordService);
        this.targetWord = targetWord;
    }

    public void setUpPlayer(Player player) {
        setupStartingWords();
        playerService.resetPlayer(player);
        player.addWords(startingWords);
        player.setStatus(PlayerStatus.PLAYING);
        player.setTargetWord(targetWord);
    }

    @Override
    public boolean winConditionReached(Player player) {
        return player.getWords().contains(player.getTargetWord());
    }
}
