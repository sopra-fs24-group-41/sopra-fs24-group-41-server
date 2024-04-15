package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final LobbyRepository lobbyRepository;

    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public List<Lobby> getPublicLobbies() {
        return lobbyRepository.findAllByPublicAccess(true);
    }

    public Lobby getLobbyByCode(long code) {
        Lobby foundLobby = lobbyRepository.findByCode(code);
        if (foundLobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Unknown lobby with code %d", code));
        }
        return foundLobby;
    }

    public Player createLobbyFromUser(User user, Boolean publicAccess) {
        String lobbyName = user.getUsername() + "'s Lobby";
        Lobby lobby = new Lobby(generateLobbyCode(), lobbyName);
        lobby.setStatus(LobbyStatus.PREGAME);
        Player player = new Player(UUID.randomUUID().toString(), user.getUsername(), lobby);

        player.setOwnedLobby(lobby);
        lobby.setOwner(player);
        lobby.setPlayers(List.of(player));
        lobby.setPublicAccess(Objects.requireNonNullElse(publicAccess, true));

        Lobby savedLobby = lobbyRepository.saveAndFlush(lobby);
        user.setPlayer(savedLobby.getOwner());
        savedLobby.getOwner().setUser(user);

        log.debug("created new lobby {}", lobby);
        log.debug("created new player from user {}", player);
        return savedLobby.getOwner();
    }

    public Player joinLobbyFromUser(User user, long lobbyCode) {
        Lobby foundLobby = lobbyRepository.findByCode(lobbyCode);
        if (foundLobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("lobby with code %d does not exist", lobbyCode));
        }
        else if (foundLobby.getStatus() != LobbyStatus.PREGAME) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "this lobby does not accept new players, wait until the game is finished");
        }

        Player player = new Player(UUID.randomUUID().toString(), user.getUsername(), foundLobby);
        player.setUser(user);
        foundLobby.getPlayers().add(player);
        user.setPlayer(player);

        log.debug("updated lobby {}", foundLobby);
        log.debug("created new player from user {}", player);
        return player;
    }

    private long generateLobbyCode() {
        long code = ThreadLocalRandom.current().nextLong(1000, 10000);
        while (lobbyRepository.existsByCode(code)) {
            code = ThreadLocalRandom.current().nextLong(1000, 10000);
        }
        return code;
    }
}
