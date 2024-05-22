package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.DailyChallengeService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DailyChallengeGameTest {
    private Player player;

    @Mock
    private PlayerService playerService;

    @Mock
    private CombinationService combinationService;

    @Mock
    private WordService wordService;

    @Mock
    private DailyChallengeService dailyChallengeService;

    private final Word targetWord = new Word("steam", 1, 0.5);

    @Spy
    @InjectMocks
    private DailyChallengeGame game;

    @BeforeEach
    void setup() {
        player = new Player();
        game = new DailyChallengeGame(playerService, combinationService, wordService, dailyChallengeService);

        MockitoAnnotations.openMocks(this);
        Mockito.when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(dailyChallengeService.getTargetWord()).thenReturn(targetWord);
    }

    @Test
    void setUpPlayer_success() {
        Mockito.doNothing().when(playerService).resetPlayer(Mockito.any());
        Word testWord = new Word("testWord", 3, 0.125);
        Mockito.when(wordService.getRandomWordWithinReachability(Mockito.anyDouble(), Mockito.anyDouble()))
                .thenReturn(testWord);

        game.setupPlayers(List.of(player));

        assertEquals(4, player.getWords().size());
        assertEquals(targetWord, player.getTargetWord());
    }

    @Test
    void makeCombination_success() {
        Word water = new Word("water", 0, 1e6);
        Word fire = new Word("fire", 0, 1e6);
        Word steam = new Word("steam", 1, 0.5);
        player.addWord(water);
        player.addWord(fire);
        player.setStatus(PlayerStatus.PLAYING);

        Mockito.doReturn(new Combination(water, fire, steam)).when(combinationService).getCombination(water, fire);

        Word result = game.makeCombination(player, List.of(water, fire)).getResult();

        assertEquals(steam, result);
    }

    @Test
    void makeCombination_wrongNumberOfWords_throwsException() {
        Word water = new Word("water", 0, 1e6);
        Word fire = new Word("fire", 0, 1e6);
        Word steam = new Word("steam", 1, 0.5);
        player.addWord(water);
        player.addWord(fire);
        player.addWord(steam);

        List<Word> words = new ArrayList<>(List.of(water, fire, steam));
        assertThrows(ResponseStatusException.class,
                () -> game.makeCombination(player, words));
    }

    @Test
    void winConditionReached_success() {
        Word word1 = new Word("palladium", 9, 0.05);
        Word word2 = new Word("mythril", 10, 0.01);

        player.addWords(List.of(word1, word2));
        player.setTargetWord(word2);

        assertTrue(game.winConditionReached(player));
    }
}
