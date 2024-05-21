package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player findPlayerByToken(String token) {
        Player foundPlayer = playerRepository.findByToken(token);
        if (foundPlayer != null) return foundPlayer;

        String errorMessage = String.format("Player with token %s not found.", token);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
    }

    public void resetPlayer(Player player) {
        player.setPoints(0);
        player.clearPlayerWords();
        player.setTargetWord(null);
        playerRepository.flush();
    }

    public Player setWinnerAndLoser(Player winner) {
        winner.setStatus(PlayerStatus.WON);
        for (Player player : winner.getLobby().getPlayers()) {
            if (player != winner) {
                player.setStatus(PlayerStatus.LOST);
            }
        }
        playerRepository.flush();
        return winner;
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
        player.getLobby().updateLastModified();
        player.getLobby().getPlayers().remove(player);
        player.setLobby(null);
        playerRepository.delete(player);
        log.debug("successfully deleted player {}", player);
    }
}
