package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;

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

    public void setupPlayers(List<Player> players) {
    }

    public Player makeCombination(Player player, List<Word> words) {
        return player;
    }

    public boolean winConditionReached(Player player) {
        return false;
    }

}
