package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {
    private final Word water = new Word("water");
    private final Word earth = new Word("earth");
    private final Word fire = new Word("fire");
    private final Word air = new Word("air");
    private final Word mud = new Word("mud");

    private Player player1;
    private Player player2;
    private List<Player> players;

    @Mock
    private PlayerService playerService;

    @Mock
    private CombinationService combinationService;

    @Mock
    private WordService wordService;

    @InjectMocks
    private Game game;

    @BeforeEach
    public void setup() {
        player1 = new Player();
        player2 = new Player();
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        MockitoAnnotations.openMocks(this);
        Mockito.when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        game.setup();
    }

    @Test
    public void setupPlayers_success() {
        game.setupPlayers(players);

        assertEquals(4, player1.getWords().size());
        assertEquals(4, player2.getWords().size());
    }
}
