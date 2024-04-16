package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private final Word water = new Word("water");
    private final Word earth = new Word("earth");
    private final Word fire = new Word("fire");
    private final Word air = new Word("air");
    private final Word mud = new Word("mud");

    private final List<Word> startingWords = new ArrayList<>();

    @Mock
    private PlayerService playerService;

    @Mock
    private CombinationService combinationService;

    @Mock
    private WordService wordService;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    public void setup() {
        startingWords.add(water);
        startingWords.add(earth);
        startingWords.add(fire);
        startingWords.add(air);

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createNewGame_success() {
        Player testPlayer = new Player();
        List<Player> testPlayers = new ArrayList<Player>();
        testPlayers.add(testPlayer);

        Lobby testLobby = new Lobby();
        testLobby.setMode(GameMode.STANDARD);
        testLobby.setPlayers(testPlayers);

        assertEquals(0, testPlayer.getWords().size());

        gameService.createNewGame(testLobby);

        assertEquals(4, testPlayer.getWords().size());
    }

    @Test
    public void play_success() {
        Player testPlayer = new Player();

        testPlayer.setWords(startingWords);

        List<Player> testPlayers = new ArrayList<Player>();
        testPlayers.add(testPlayer);

        Lobby testLobby = new Lobby();
        testLobby.setMode(GameMode.STANDARD);

        testPlayer.setLobby(testLobby);

        List<Word> playingWords = new ArrayList<>();
        playingWords.add(water);
        playingWords.add(earth);

        Combination testCombination = new Combination(water, earth, mud);

        Mockito.when(playerService.findPlayer(testPlayer)).thenReturn(testPlayer);
        Mockito.when(combinationService.getCombination(water, earth)).thenReturn(testCombination);

        assertEquals(startingWords, testPlayer.getWords());

        gameService.play(testPlayer, playingWords);

        assertEquals(mud, testPlayer.getWords().get(4));
    }
}
