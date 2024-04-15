package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository, PlayerWordRepository playerWordRepository, WordService wordService) {
        this.playerRepository = playerRepository;
    }

    public Player findPlayer(Player player) {
        Player foundPlayer = playerRepository.findByToken(player.getToken());
        if (foundPlayer != null) return foundPlayer;

        String errorMessage = String.format("Player %s not found.", player.getName());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
    }
}
