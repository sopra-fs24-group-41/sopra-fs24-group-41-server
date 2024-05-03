package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    public void setup() {
        startingWords.add(water);
        startingWords.add(earth);
        startingWords.add(fire);
        startingWords.add(air);

        MockitoAnnotations.openMocks(this);
        Mockito.when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
    }

    @Test
    public void createNewGame_success() {
        Player testPlayer1 = new Player();
        Player testPlayer2 = new Player();

        List<Player> testPlayers = new ArrayList<Player>();
        testPlayers.add(testPlayer1);
        testPlayers.add(testPlayer2);

        Lobby testLobby = new Lobby();
        testLobby.setMode(GameMode.STANDARD);
        testLobby.setPlayers(testPlayers);

        assertEquals(0, testPlayer1.getWords().size());
        assertEquals(0, testPlayer2.getWords().size());

        Mockito.when(playerService.resetPlayer(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        gameService.createNewGame(testLobby);

        assertEquals(4, testPlayer1.getWords().size());
        assertEquals(4, testPlayer2.getWords().size());
    }

    @Test
    public void play_success() {
        Player testPlayer1 = new Player();
        Player testPlayer2 = new Player();

        testPlayer1.addWords(startingWords);
        testPlayer2.addWords(startingWords);

        List<Player> testPlayers = new ArrayList<Player>();
        testPlayers.add(testPlayer1);
        testPlayers.add(testPlayer2);

        Lobby testLobby = new Lobby();
        testLobby.setMode(GameMode.STANDARD);

        testPlayer1.setLobby(testLobby);

        List<Word> playingWords = new ArrayList<>();
        playingWords.add(water);
        playingWords.add(earth);

        Combination testCombination = new Combination(water, earth, mud);
        Mockito.when(wordService.saveWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(combinationService.getCombination(water, earth)).thenReturn(testCombination);
        Mockito.doNothing().when(messagingTemplate).convertAndSend(Mockito.any());

        assertEquals(startingWords, testPlayer1.getWords());

        gameService.play(testPlayer1, playingWords);

        assertEquals(mud, testPlayer1.getWords().get(4));
    }
}
