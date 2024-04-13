package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player checkToken(String token) {
        Player foundPlayer = playerRepository.findByToken(token);
        if (foundPlayer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No player found with this token");
        }
        log.debug("found the player for the provided token: {}", foundPlayer);
        return foundPlayer;
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