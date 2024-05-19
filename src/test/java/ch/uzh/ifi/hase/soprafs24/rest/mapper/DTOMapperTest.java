package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation works.
 */
class DTOMapperTest {

    // tests convertUserPostDTOtoEntity
    @Test
    void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
        userLoginPostDTO.setPassword("password");
        userLoginPostDTO.setUsername("username");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userLoginPostDTO);

        // check content
        assertEquals(userLoginPostDTO.getPassword(), user.getPassword());
        assertEquals(userLoginPostDTO.getUsername(), user.getUsername());
    }

    // tests convertEntityToUserGetDTO
    @Test
    void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();
        user.setPassword("password");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
    }

    // tests convertEntityToUserSecretGetDTO
    @Test
    void testLogInUser_fromUser_toUserSecretDTO_success() {
        User user = new User();
        user.setToken("1");

        UserSecretDTO userSecretDTO = DTOMapper.INSTANCE.convertEntityToUserSecretGetDTO(user);

        assertEquals(user.getToken(), userSecretDTO.getToken());
    }

    // tests convertUserTokenPostDTOtoEntity
    @Test
    void testCreateUser_fromUserTokenPostDTO_toUser_success() {
        // create UserTokenPostDTO
        UserTokenPostDTO userTokenPostDTO = new UserTokenPostDTO();
        userTokenPostDTO.setToken("token");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserTokenPostDTOtoEntity(userTokenPostDTO);

        // check content
        assertEquals(userTokenPostDTO.getToken(), user.getToken());
    }

    // tests convertEntityToLobbyGetDTO
    @Test
    void testGetLobbies_fromLobby_toLobbyGetDTO_success() {
        Lobby testLobby = new Lobby(1234, "test Lobby");
        testLobby.setMode(GameMode.STANDARD);

        Player testPlayer1 = new Player("123", "testplayer", null);
        testPlayer1.setId(4);
        testPlayer1.setPoints(32);
        // no value for AvailableWords set

        Player testPlayer2 = new Player("643", "anothertestplayer", null);
        testPlayer2.setId(5);
        testPlayer2.setPoints(54);

        User testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setPassword("testPassword");
        testUser1.setUsername("firstname@lastname");
        testUser1.setStatus(UserStatus.OFFLINE);
        testUser1.setToken("1");

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setPassword("testPassword2");
        testUser2.setUsername("firstname@lastname2");
        testUser2.setStatus(UserStatus.OFFLINE);
        testUser2.setToken("2");

        testUser1.setPlayer(testPlayer1);
        testPlayer1.setUser(testUser1);

        testUser2.setPlayer(testPlayer2);
        testPlayer2.setUser(testUser2);

        testLobby.setOwner(testPlayer1);
        testPlayer1.setOwnedLobby(testLobby);

        testPlayer1.setLobby(testLobby);
        testPlayer2.setLobby(testLobby);
        testLobby.setPlayers(Arrays.asList(testPlayer1, testPlayer2));

        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(testLobby);

        assertEquals(testLobby.getCode(), lobbyGetDTO.getCode());
        assertEquals(testLobby.getName(), lobbyGetDTO.getName());
        assertEquals(testLobby.getPublicAccess(), lobbyGetDTO.getPublicAccess());
        assertEquals(testLobby.getStatus(), lobbyGetDTO.getStatus());
        assertEquals(testLobby.getMode(), lobbyGetDTO.getMode());
    }

    // tests convertEntityToPlayerJoinedDTO
    @Test
    void testCreateLobby_fromPlayerAndLobby_toPlayerJoinedDTO() {
        Lobby testLobby = new Lobby(1234, "test Lobby");
        testLobby.setMode(GameMode.STANDARD);

        Player testPlayer = new Player("123", "testplayer", null);
        testPlayer.setId(4);
        testPlayer.setPoints(32);
        // no value for AvailableWords set

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");

        testUser.setPlayer(testPlayer);
        testPlayer.setUser(testUser);

        testLobby.setOwner(testPlayer);
        testPlayer.setOwnedLobby(testLobby);

        testPlayer.setLobby(testLobby);
        testLobby.setPlayers(Collections.singletonList(testPlayer));

        PlayerJoinedDTO playerJoinedDTO = DTOMapper.INSTANCE.convertEntityToPlayerJoinedDTO(testPlayer);

        assertEquals(testPlayer.getToken(), playerJoinedDTO.getPlayerToken());
    }

    // tests convertEntityToPlayerWordDTO
    @Test
    void testConvertFromPlayerWordToPlayerWordDTO() {
        PlayerWord testPlayerWord = new PlayerWord(new Player("123", "testplayer", null),
                new Word("testWord"));

        PlayerWordDTO playerWordDTO = DTOMapper.INSTANCE.convertEntityToPlayerWordDTO(testPlayerWord);

        assertEquals(testPlayerWord.getWord().getName(), playerWordDTO.getWord().getName());
        assertEquals(testPlayerWord.getUses(), playerWordDTO.getUses());
        assertEquals(testPlayerWord.getTimestamp(), playerWordDTO.getTimestamp());
    }

    // tests convertEntityToWordDTO
    @Test
    void testConvertWordToWordDTO() {
        Word testWord = new Word("testWord");

        WordDTO wordDTO = DTOMapper.INSTANCE.convertEntityToWordDTO(testWord);

        assertEquals(testWord.getName(), wordDTO.getName());
        assertEquals(testWord.isNewlyDiscovered(), wordDTO.isNewlyDiscovered());
    }

    // tests convertEntityToPlayerPlayedDTO
    @Test
    void testConvertPlayerToPlayerPlayedDTO() {
        Player testPlayer = new Player("123", "testplayer", null);
        testPlayer.setId(4);
        testPlayer.setPoints(32);

        PlayerPlayedDTO playerPlayedDTO = DTOMapper.INSTANCE.convertEntityToPlayerPlayedDTO(testPlayer);

        assertEquals(testPlayer.getPoints(), playerPlayedDTO.getPoints());
        assertEquals(testPlayer.getPlayerWords().size(), playerPlayedDTO.getPlayerWords().size());
        assertEquals(testPlayer.getStatus(), playerPlayedDTO.getStatus());
    }

    // tests convertEntityToPlayerGetDTO
    @Test
    void testConvertPlayerToPlayerGetDTO() {
        Player testPlayer = new Player("123", "testplayer", null);
        testPlayer.setId(4);
        testPlayer.setPoints(32);

        User testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("firstname@lastname");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("1");

        testUser.setPlayer(testPlayer);
        testPlayer.setUser(testUser);

        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(testPlayer);

        assertEquals(testPlayer.getId(), playerGetDTO.getId());
        assertEquals(testPlayer.getName(), playerGetDTO.getName());
        assertEquals(testPlayer.getPoints(), playerGetDTO.getPoints());
        assertEquals(testPlayer.getPlayerWords().size(), playerGetDTO.getPlayerWords().size());
        assertEquals(testPlayer.getStatus(), playerGetDTO.getStatus());
        assertEquals(testPlayer.getUser().getUsername(), playerGetDTO.getUser().getUsername());
    }

    // tests convertUserPutDTOtoEntity
    @Test
    void testConvertUserPutDTOtoEntity() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setFavourite("favourite");
        userPutDTO.setUsername("username");

        User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        assertEquals(userPutDTO.getFavourite(), user.getFavourite());
        assertEquals(userPutDTO.getUsername(), user.getUsername());
    }
}
