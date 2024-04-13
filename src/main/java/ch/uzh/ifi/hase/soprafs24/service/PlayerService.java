package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.WordNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final WordService wordService;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository, WordService wordService) {
        this.playerRepository = playerRepository;
        this.wordService = wordService;
    }

    public Player updatePlayer(Player player) {
        Player updatedPlayer = findPlayer(player);
        updatedPlayer.setAvailableWords(player.getAvailableWords());
        updatedPlayer.setPoints(player.getPoints());
        playerRepository.saveAndFlush(updatedPlayer);
        return updatedPlayer;
    }

    public Player addWordToPlayer(Player player, Word word) {
        Player updatedPlayer = findPlayer(player);
        updatedPlayer.addWord(wordService.getWord(word));
        playerRepository.saveAndFlush(updatedPlayer);
        return updatedPlayer;
    }

    public Player addPointsToPlayer(Player player, Long points) {
        Player updatedPlayer = findPlayer(player);
        updatedPlayer.addPoints(points);
        playerRepository.saveAndFlush(updatedPlayer);
        return updatedPlayer;
    }

    public Player findPlayer(Player player) {
        Player foundPlayer = playerRepository.findByToken(player.getToken());

        if (foundPlayer != null) return foundPlayer;

        String errorMessage = String.format("Player %s not found.", player.getName());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
    }
}
