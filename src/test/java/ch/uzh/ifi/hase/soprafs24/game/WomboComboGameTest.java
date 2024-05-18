package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WomboComboGameTest {
    private Player player1;
    private Player player2;
    private List<Player> players;
    private Lobby lobby = new Lobby();


    @Mock
    private PlayerService playerService;

    @Mock
    private CombinationService combinationService;

    @Mock
    private WordService wordService;

    @InjectMocks
    private WomboComboGame game;

    @BeforeEach
    void setup() {
        player1 = new Player();
        player2 = new Player();
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        lobby = new Lobby(1234, "test lobby");
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
        Mockito.doReturn(testWord)
                .when(wordService).getRandomWordWithinReachability(Mockito.anyDouble(), Mockito.anyDouble());
        game.setupPlayers(players);

        assertEquals(4, player1.getWords().size());
        assertEquals(4, player2.getWords().size());
        assertEquals(testWord, player1.getTargetWord());
        assertEquals(testWord, player2.getTargetWord());
    }

    @Test
    void setNewTargetWord_multipleIterations_success() {
        Word commonWord = new Word("water", 0, 1e6);
        Word commonWord1 = new Word("mud", 1, 0.5);
        Word rareWord = new Word("adamantium", 5, 0.05);

        player1.addWord(commonWord);
        player1.addWord(commonWord1);
        Mockito.doReturn(player1.getWords().get(1))
                .when(wordService).getRandomWordWithinReachability(AdditionalMatchers.gt(0.05), Mockito.anyDouble());
        Mockito.doReturn(rareWord)
                .when(wordService).getRandomWordWithinReachability(AdditionalMatchers.leq(0.05), Mockito.anyDouble());

        game.setNewTargetWord(player1);

        assertEquals(rareWord, player1.getTargetWord());
    }

    @Test
    void winConditionReached_success() {
        player1.setPoints(55);

        boolean result = game.winConditionReached(player1);

        assertTrue(result);
        assertEquals(PlayerStatus.WON, player1.getStatus());
        assertEquals(PlayerStatus.LOST, player2.getStatus());
    }
}
