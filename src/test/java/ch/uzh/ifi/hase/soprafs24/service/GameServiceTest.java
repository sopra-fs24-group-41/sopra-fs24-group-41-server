package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.websocket.TimeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameServiceTest {

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private SimpMessagingTemplate messagingTemplateMock;


    private final Word water = new Word("water", 0, 1e6);
    private final Word earth = new Word("earth", 0, 1e6);
    private final Word fire = new Word("fire");
    private final Word air = new Word("air");
    private final Word mud = new Word("mud", 1, 0.5);

    private final List<Word> startingWords = new ArrayList<>();

    @Mock
    private PlayerService playerService;

    @Mock
    private CombinationService combinationService;

    @Mock
    private WordService wordService;

    @Mock
    private LobbyService lobbyService;

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
        when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
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

        Mockito.doNothing().when(playerService).resetPlayer(Mockito.any());
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
        when(wordService.saveWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        when(combinationService.getCombination(water, earth)).thenReturn(testCombination);
        Mockito.doNothing().when(messagingTemplate).convertAndSend(Mockito.any());

        assertEquals(startingWords, testPlayer1.getWords());

        gameService.play(testPlayer1, playingWords);

        assertEquals(mud, testPlayer1.getWords().get(4));
    }
  
    @Test
    void playerWins_updateWinsAndLossesCount_success() {
        Lobby testLobby = new Lobby();
        testLobby.setMode(GameMode.WOMBOCOMBO);
        testLobby.setPlayers(new ArrayList<>());

        List<User> testUsers = Arrays.asList(new User(), new User(), new User());
        List<Player> testPlayers = new ArrayList<>();
        for (User testUser : testUsers) {
            Player testPlayer = new Player();

            testPlayer.setUser(testUser);
            testPlayer.setLobby(testLobby);

            testUser.setPlayer(testPlayer);

            testPlayers.add(testPlayer);
            testLobby.getPlayers().add(testPlayer);
        }

        gameService.updateWinsAndLosses(testPlayers.get(0), testLobby);

        assertEquals(1, testUsers.get(0).getWins());
        assertEquals(0, testUsers.get(1).getWins());
        assertEquals(0, testUsers.get(2).getWins());

        assertEquals(0, testUsers.get(0).getLosses());
        assertEquals(1, testUsers.get(1).getLosses());
        assertEquals(1, testUsers.get(2).getLosses());
    }

    @Test
    void playerWins_updateWinsAndLossesCountAndExcludesAnonPlayers_success() {
        Lobby testLobby = new Lobby();
        testLobby.setMode(GameMode.WOMBOCOMBO);
        testLobby.setPlayers(new ArrayList<>());

        List<User> testUsers = Arrays.asList(new User(), new User(), new User());
        List<Player> testPlayers = new ArrayList<>();
        for (User testUser : testUsers) {
            Player testPlayer = new Player();

            testPlayer.setUser(testUser);
            testPlayer.setLobby(testLobby);

            testUser.setPlayer(testPlayer);

            testPlayers.add(testPlayer);
            testLobby.getPlayers().add(testPlayer);
        }

        Player anonTestPlayer = new Player();
        anonTestPlayer.setLobby(testLobby);
        testPlayers.add(anonTestPlayer);
        testLobby.getPlayers().add(anonTestPlayer);

        gameService.updateWinsAndLosses(testPlayers.get(0), testLobby);

        assertEquals(1, testUsers.get(0).getWins());
        assertEquals(0, testUsers.get(1).getWins());
        assertEquals(0, testUsers.get(2).getWins());

        assertEquals(0, testUsers.get(0).getLosses());
        assertEquals(1, testUsers.get(1).getLosses());
        assertEquals(1, testUsers.get(2).getLosses());
    }

    @Test
    void play_updatesPlayerStatistics_success() {
        User user = new User();
        Player player = new Player();

        player.setUser(user);
        user.setPlayer(player);

        mud.setNewlyDiscovered(true);

        gameService.updatePlayerStatistics(player, mud);

        assertEquals(1, user.getCombinationsMade());
        assertEquals(1, user.getDiscoveredWords());
        assertEquals(mud, user.getRarestWordFound());
    }


    @Test //This is a unit test
    public void gameTask_game_end_after_1_minute() {
        Lobby testLobby = mock(Lobby.class);
        testLobby.setStatus(LobbyStatus.INGAME);
        when(testLobby.getCode()).thenReturn(1234L);
        when(testLobby.getGameTime()).thenReturn(60); // Mock gameTime for 1 minute

        Timer gameTimer = new Timer();
        TimerTask gameTask = gameService.createGameTask(testLobby);
        gameTimer.scheduleAtFixedRate(gameTask, 3000, 1000); //Accelerate timer to run task every second, original implement does it every 10th second

        verify(messagingTemplateMock, timeout(1000 * 20).times(3)).convertAndSend(eq("/topic/lobbies/1234/game"), any(TimeDTO.class));
    }
}

