package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserLoginPostDTO userLoginPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "profilePicture", target = "profilePicture")
    @Mapping(source = "wins", target = "wins")
    @Mapping(source = "losses", target = "losses")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "favourite", target = "favourite")
    @Mapping(source = "combinationsMade", target = "combinationsMade")
    @Mapping(source = "discoveredWords", target = "discoveredWords")
    @Mapping(source = "rarestWordFound", target = "rarestWordFound")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "id", target = "id")
    UserSecretDTO convertEntityToUserSecretGetDTO(User user);

    @Mapping(source = "token", target = "token")
    User convertUserTokenPostDTOtoEntity(UserTokenPostDTO userTokenPostDTO);

    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "publicAccess", target = "publicAccess")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "mode", target = "mode")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "players", target = "players")
    LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

    @Mapping(source = "token", target = "playerToken")
    @Mapping(source = "id", target = "playerId")
    @Mapping(source = "lobby", target = "lobby")
    PlayerJoinedDTO convertEntityToPlayerJoinedDTO(Player player);

    @Mapping(source = "word", target = "word")
    @Mapping(source = "timestamp", target = "timestamp")
    PlayerWordDTO convertEntityToPlayerWordDTO(PlayerWord playerWord);

    @Mapping(source = "name", target = "name")
    WordDTO convertEntityToWordDTO(Word word);

    @Mapping(source = "points", target = "points")
    @Mapping(source = "playerWords", target = "playerWords")
    @Mapping(source = "targetWord", target = "targetWord")
    PlayerPlayedDTO convertEntityToPlayerPlayedDTO(Player player);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "points", target = "points")
    @Mapping(source = "playerWords", target = "playerWords")
    @Mapping(source = "targetWord", target = "targetWord")
    PlayerGetDTO convertEntityToPlayerGetDTO(Player player);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "favourite", target = "favourite")
    @Mapping(source = "profilePicture", target = "profilePicture")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

}
