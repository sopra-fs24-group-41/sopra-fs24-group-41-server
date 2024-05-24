package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Instruction;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.websocket.InstructionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;


/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PlayerService playerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;

    private static final String MESSAGE_LOBBY_BASE = "/topic/lobbies";
    private static final String MESSAGE_LOBBY_CODE = "/topic/lobbies/%d";
    private static final String MESSAGE_LOBBY_GAME = "/topic/lobbies/%d/game";

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository, PlayerService playerService, SimpMessagingTemplate messagingTemplate, LobbyService lobbyService) {
        this.userRepository = userRepository;
        this.playerService = playerService;
        this.messagingTemplate = messagingTemplate;
        this.lobbyService = lobbyService;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
    public User getUserById(Long id) {
        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isEmpty()) {
            String errorMessage = "Cannot find this username.";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
        return foundUser.get();
    }

    public User createUser(User newUser) {
        validateUsername(newUser.getUsername());
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setProfilePicture("bluefrog");
        newUser.setCreationDate(LocalDate.now());
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User logInUser(User userCredentials) {
        User userByUsername = userRepository.findByUsername(userCredentials.getUsername());

        if (userByUsername == null) {
            String errorMessage = "Cannot find this username.";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
        if (!Objects.equals(userCredentials.getPassword(), userByUsername.getPassword())) {
            String errorMessage = "Wrong password";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }

        userByUsername.setStatus(UserStatus.ONLINE);
        return userByUsername;
    }

    public void logOutUser(String userToken) {
        User userByToken = userRepository.findByToken(userToken);

        if (userByToken == null) {
            String errorMessage = "Logging out unsuccessful: Unknown user (invalid token).";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }

        userByToken.setStatus(UserStatus.OFFLINE);
    }

    public User checkToken(String userToken) {
        User userByToken = userRepository.findByToken(userToken);

        if (userByToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user token has insufficient access rights");
        }
        return userByToken;
    }

    public User authUser(Long id, String token) {
        User foundUser = userRepository.findByToken(token);
        if (foundUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!Objects.equals(foundUser.getId(), id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permission to access this user's data");
        }
        return foundUser;
    }

    public void validateUsername(String username) {
        if (username == null || username.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username may not be left empty");
        }

        if (username.contains(" ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username may not contain blank spaces");
        }

        if (username.length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be more than 20 characters.");
        }

        User userByUsername = userRepository.findByUsername(username);
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique!");
        }
    }

    public void favouriteValidation(String favourite) {
        if(favourite != null && favourite.contains(" ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Favourite word may not contain blank spaces");
        }
    }

    public User editUser(String token, User updatedUser) {
        User foundUser = userRepository.findByToken(token);

        //Input validation
        validateUsername(updatedUser.getUsername());
        favouriteValidation(updatedUser.getFavourite());

        //Update data
        if(updatedUser.getUsername()!=null && !Objects.equals(foundUser.getUsername(), updatedUser.getUsername())){
            foundUser.setUsername(updatedUser.getUsername());}

        if(updatedUser.getFavourite()!=null && !Objects.equals(foundUser.getFavourite(), updatedUser.getFavourite())){
            foundUser.setFavourite(updatedUser.getFavourite());}
        if(updatedUser.getFavourite()!=null && updatedUser.getFavourite().isEmpty()){
            foundUser.setFavourite("Zaddy");
        }

        if(updatedUser.getProfilePicture()!=null && !Objects.equals(foundUser.getProfilePicture(), updatedUser.getProfilePicture())){
            foundUser.setProfilePicture(updatedUser.getProfilePicture());
        }
        return foundUser;
    }

    public void deleteUser(User user) {
        Player player = user.getPlayer();
        if (player != null) {
            if (player.getOwnedLobby() == null) {
                Lobby lobby = player.getLobby();
                playerService.removePlayer(player);
                messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_CODE, lobby.getCode()),
                        new InstructionDTO(Instruction.UPDATE_LOBBY, DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby)));
            }
            else {
                lobbyService.removeLobby(player.getOwnedLobby());
                messagingTemplate.convertAndSend(MESSAGE_LOBBY_BASE,
                        new InstructionDTO(Instruction.UPDATE_LOBBY_LIST, lobbyService.getPublicLobbies().stream().map(DTOMapper.INSTANCE::convertEntityToLobbyGetDTO).toList()));
                messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, player.getOwnedLobby().getCode()),
                        new InstructionDTO(Instruction.KICK, null, "The lobby was closed by the owner"));
            }
        }
        userRepository.delete(user);
    }
}
