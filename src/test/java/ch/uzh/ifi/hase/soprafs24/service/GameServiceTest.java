package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.websocket.InstructionDTO;
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

class GameServiceTest {

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

    @Mock
    private AchievementService achievementService;

    @Mock
    private PlatformTransactionManager transactionManager;

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
    void createNewGame_success() {
        Player testPlayer1 = new Player();
        Player testPlayer2 = new Player();

        List<Player> testPlayers = new ArrayList<>();
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
    void play_success() {
        Player testPlayer1 = new Player();
        Player testPlayer2 = new Player();

        testPlayer1.addWords(startingWords);
        testPlayer2.addWords(startingWords);

        List<Player> testPlayers = new ArrayList<>();
        testPlayers.add(testPlayer1);
        testPlayers.add(testPlayer2);

        Lobby testLobby = new Lobby();
        testLobby.setMode(GameMode.STANDARD);
        testLobby.setPlayers(testPlayers);

        testPlayer1.setLobby(testLobby);

        List<Word> playingWords = new ArrayList<>();
        playingWords.add(water);
        playingWords.add(earth);

        Combination testCombination = new Combination(water, earth, mud);
        when(wordService.saveWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        when(combinationService.getCombination(water, earth)).thenReturn(testCombination);

        assertEquals(startingWords, testPlayer1.getWords());

        gameService.play(testPlayer1, playingWords);

        assertEquals(mud, testPlayer1.getWords().get(4));
    }

    @Test
    void play_won_success() {
        Lobby testLobby = new Lobby(1234, "testLobby");
        testLobby.setMode(GameMode.FUSIONFRENZY);
        testLobby.setStatus(LobbyStatus.INGAME);

        Player testPlayer1 = new Player("123", "testPlayer1", testLobby);
        Player testPlayer2 = new Player("234", "testPlayer2", testLobby);

        testPlayer1.addWords(startingWords);
        testPlayer2.addWords(startingWords);

        List<Player> testPlayers = new ArrayList<>();
        testPlayers.add(testPlayer1);
        testPlayers.add(testPlayer2);

        testLobby.setPlayers(testPlayers);

        testPlayer1.setLobby(testLobby);
        testPlayer1.setTargetWord(mud);

        List<Word> playingWords = new ArrayList<>();
        playingWords.add(water);
        playingWords.add(earth);

        Combination testCombination = new Combination(water, earth, mud);
        when(wordService.saveWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        when(combinationService.getCombination(water, earth)).thenReturn(testCombination);
        when(playerService.setWinnerAndLoser(Mockito.any())).thenAnswer(invocation -> {
            Player player = invocation.getArgument(0);
            player.setStatus(PlayerStatus.WON);
            return player;
        });

        assertEquals(startingWords, testPlayer1.getWords());

        gameService.play(testPlayer1, playingWords);

        assertEquals(mud, testPlayer1.getWords().get(4));
        verify(messagingTemplate, Mockito.times(2)).convertAndSend(Mockito.anyString(), (Object) Mockito.any());
        assertEquals(PlayerStatus.WON, testPlayer1.getStatus());
        assertEquals(LobbyStatus.PREGAME, testLobby.getStatus());
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

            testPlayer.setStatus(PlayerStatus.LOST);
            testPlayer.setUser(testUser);
            testPlayer.setLobby(testLobby);

            testUser.setPlayer(testPlayer);

            testPlayers.add(testPlayer);
            testLobby.getPlayers().add(testPlayer);
        }
        testPlayers.get(0).setStatus(PlayerStatus.WON);

        gameService.updateWinsAndLosses(testLobby);

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

            testPlayer.setStatus(PlayerStatus.LOST);
            testPlayer.setUser(testUser);
            testPlayer.setLobby(testLobby);

            testUser.setPlayer(testPlayer);

            testPlayers.add(testPlayer);
            testLobby.getPlayers().add(testPlayer);
        }
        testPlayers.get(0).setStatus(PlayerStatus.WON);

        Player anonTestPlayer = new Player();
        anonTestPlayer.setLobby(testLobby);
        testPlayers.add(anonTestPlayer);
        testLobby.getPlayers().add(anonTestPlayer);

        gameService.updateWinsAndLosses(testLobby);

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
    void gameTask_game_end_after_1_minute() {
        Lobby testLobby = mock(Lobby.class);
        testLobby.setStatus(LobbyStatus.INGAME);
        when(testLobby.getCode()).thenReturn(1234L);
        when(testLobby.getGameTime()).thenReturn(60); // Mock gameTime for 1 minute

        Timer gameTimer = new Timer();
        TimerTask gameTask = gameService.createGameTask(testLobby);
        gameTimer.scheduleAtFixedRate(gameTask, 3000, 1000); //Accelerate timer to run task every second, original implement does it every 10th second

        verify(messagingTemplate, timeout(1000 * 20).times(3)).convertAndSend(eq("/topic/lobbies/1234/game"), any(InstructionDTO.class));
    }
}

