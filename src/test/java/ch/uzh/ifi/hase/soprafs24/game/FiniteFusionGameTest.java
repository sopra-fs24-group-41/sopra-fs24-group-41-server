package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FiniteFusionGameTest {
    private Player player1;
    private Player player2;
    private List<Player> players;

    @Mock
    private PlayerService playerService;

    @Mock
    private CombinationService combinationService;

    @Mock
    private WordService wordService;

    @Spy
    @InjectMocks
    private FiniteFusionGame game;

    @BeforeEach
    void setup() {
        Lobby lobby = new Lobby(1234, "test lobby");
        player1 = new Player("1234", "testPLayer1", lobby);
        player2 = new Player("2345", "testPlayer2", lobby);
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        lobby.setPlayers(new ArrayList<>());
        for (Player player : players) {
            lobby.addPlayer(player);
            player.setLobby(lobby);
        }

        MockitoAnnotations.openMocks(this);
        Mockito.when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
    }

    @Test
    void setupPlayers_success() {
        Mockito.doNothing().when(playerService).resetPlayer(Mockito.any());
        Word testWord = new Word("testWord", 3, 0.125);
        Mockito.when(wordService.selectTargetWord(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt())).thenReturn(testWord);

        game.setupPlayers(players);

        assertEquals(4, player1.getWords().size());
        assertEquals(4, player2.getWords().size());
        assertEquals(testWord, player1.getTargetWord());
        assertEquals(testWord, player2.getTargetWord());
    }

    @Test
    void playerLoses_success() {
        Mockito.doNothing().when(playerService).resetPlayer(Mockito.any());

        game.playerLoses(player1);

        assertEquals(PlayerStatus.LOST, player1.getStatus());
    }

    @Test
    void makeCombination_success() {
        Word water = new Word("water", 0, 1e6);
        Word fire = new Word("fire", 0, 1e6);
        Word steam = new Word("steam", 1, 0.5);
        player1.addWord(water);
        player1.addWord(fire);
        player1.setStatus(PlayerStatus.PLAYING);

        Mockito.doReturn(new Combination(water, fire, steam)).when(combinationService).getCombination(water, fire);
        Mockito.doReturn(new Combination(water, fire, steam)).when(game).playFiniteFusion(Mockito.any(), Mockito.any());

        Word result = game.makeCombination(player1, List.of(water, fire)).getResult();

        assertEquals(steam, result);
    }

    @Test
    void makeCombination_wrongNumberOfWords_throwsException() {
        Word water = new Word("water", 0, 1e6);
        Word fire = new Word("fire", 0, 1e6);
        Word steam = new Word("steam", 1, 0.5);
        player1.addWord(water);
        player1.addWord(fire);
        player1.addWord(steam);

        List<Word> words = new ArrayList<>(List.of(water, fire, steam));
        assertThrows(ResponseStatusException.class,
                () -> game.makeCombination(player1, words));
    }

    @Test
    void playFiniteFusion_noUsesLeft_success() {
        Word water = new Word("water", 0, 1e6);
        Word fire = new Word("fire", 0, 1e6);

        player1.addWord(water, 1);
        player1.addWord(fire, 0);

        List<Word> words = new ArrayList<>(List.of(water, fire));
        assertThrows(ResponseStatusException.class, () -> game.playFiniteFusion(player1, words));
    }

    @Test
    void playFiniteFusion_success() {
        Word water = new Word("water", 0, 1e6);
        Word fire = new Word("fire", 0, 1e6);
        Word steam = new Word("steam", 1, 0.5);
        player1.addWord(water, 3);
        player1.addWord(fire, 3);

        Mockito.doReturn(new Combination(water, fire, steam)).when(combinationService).getCombination(water, fire);

        game.playFiniteFusion(player1, List.of(water, fire));

        assertEquals(1, player1.getPoints());
        assertTrue(player1.getWords().contains(steam));
    }

    @Test
    void winConditionReached_success() {
        Word word1 = new Word("palladium", 9, 0.05);
        Word word2 = new Word("mythril", 10, 0.01);

        player1.addWords(List.of(word1, word2));
        player1.setTargetWord(word2);
        player1.setStatus(PlayerStatus.PLAYING);

        boolean result = game.winConditionReached(player1);

        assertTrue(result);
    }
}
