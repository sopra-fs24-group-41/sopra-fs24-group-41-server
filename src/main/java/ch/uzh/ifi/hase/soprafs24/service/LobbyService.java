package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final LobbyRepository lobbyRepository;

    private final PlayerService playerService;

    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository, PlayerService playerService) {
        this.lobbyRepository = lobbyRepository;
        this.playerService = playerService;
    }

    public List<Lobby> getPublicLobbies() {
        return lobbyRepository.findAllByPublicAccess(true);
    }

    public boolean allPlayersReady(Lobby lobby) {
        return lobby.getPlayers().stream().allMatch(player -> player.getStatus() == PlayerStatus.READY);
    }

    public boolean allPlayersLost(Lobby lobby) {
        return lobby.getPlayers().stream().allMatch(player -> player.getStatus() == PlayerStatus.LOST);
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

    public Map<String, Boolean> updateLobby(Lobby lobby, LobbyPutDTO lobbyPutDTO) {
        Map<String, Boolean> updates = new HashMap<>();
        updates.put("mode", false);
        updates.put("name", false);
        updates.put("publicAccess", false);

        if (lobbyPutDTO.getMode() != null && !lobbyPutDTO.getMode().equals(lobby.getMode())) {
            lobby.setMode(lobbyPutDTO.getMode());
            updates.put("mode", true);
        }
        if (lobbyPutDTO.getName() != null && !Objects.equals(lobbyPutDTO.getName(), lobby.getName())) {
            lobby.setName(lobbyPutDTO.getName());
            updates.put("name", true);
        }
        if (lobbyPutDTO.getPublicAccess() != null && !Objects.equals(lobbyPutDTO.getPublicAccess(), lobby.getPublicAccess())) {
            lobby.setPublicAccess(lobbyPutDTO.getPublicAccess());
            updates.put("publicAccess", true);
        }
        return updates;
    }

    public void removeLobby(Lobby lobby) {
        if (lobby.getOwner() != null) {
            lobby.getOwner().setOwnedLobby(null);
            lobby.setOwner(null);
        }
        if (lobby.getPlayers() != null) {
            List<Player> playerList = lobby.getPlayers();
            for (int i = playerList.toArray().length-1; i>=0; i--) {
                playerService.removePlayer(playerList.get(i));
            }
        }
        lobbyRepository.delete(lobby);
        log.debug("successfully deleted lobby {}", lobby);
    }

    private long generateLobbyCode() {
        long code = ThreadLocalRandom.current().nextLong(1000, 10000);
        while (lobbyRepository.existsByCode(code)) {
            code = ThreadLocalRandom.current().nextLong(1000, 10000);
        }
        return code;
    }
}
