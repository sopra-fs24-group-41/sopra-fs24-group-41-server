package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.PlayerWord;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerWordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final PlayerRepository playerRepository;
    private final PlayerWordRepository playerWordRepository;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository, PlayerWordRepository playerWordRepository, WordService wordService, PlayerWordRepository playerWordRepository1) {
        this.playerRepository = playerRepository;
        this.playerWordRepository = playerWordRepository1;
    }

    public Player findPlayerByToken(String token) {
        Player foundPlayer = playerRepository.findByToken(token);
        if (foundPlayer != null) return foundPlayer;

        String errorMessage = String.format("Player with token %s not found.", token);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
    }

    public Player resetPlayer(Player player) {
        player.setPoints(0);
        playerWordRepository.deleteAllByPlayer(player);
        playerWordRepository.flush();
        return playerRepository.save(player);
    }

    public void removePlayer(Player player) {
        if (player.getUser() != null) {
            User user = player.getUser();
            user.setPlayer(null);
            player.setUser(null);
        }
        if (player.getOwnedLobby() != null) {
            player.getOwnedLobby().setOwner(null);
            player.setOwnedLobby(null);
        }
        player.getLobby().getPlayers().remove(player);
        player.setLobby(null);
        playerRepository.delete(player);
        log.debug("successfully deleted player {}", player);
    }
}
