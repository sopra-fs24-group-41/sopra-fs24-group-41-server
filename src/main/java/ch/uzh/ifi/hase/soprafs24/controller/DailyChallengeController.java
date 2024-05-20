package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.DailyChallengeRecord;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DailyChallengeRecordGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerJoinedDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPlayedDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.DailyChallengeService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DailyChallengeController {
    private final UserService userService;

    private final PlayerService playerService;

    private final DailyChallengeService dailyChallengeService;

    DailyChallengeController(UserService userService, PlayerService playerService, DailyChallengeService dailyChallengeService) {
        this.userService = userService;
        this.playerService = playerService;
        this.dailyChallengeService = dailyChallengeService;
    }

    @PostMapping("/challenges/players")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerJoinedDTO startChallenge(@RequestHeader String userToken) {
        if (userToken == null || userToken.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User token is empty!");

        User user = userService.checkToken(userToken);
        Player player = dailyChallengeService.startGame(user);

        return DTOMapper.INSTANCE.convertEntityToPlayerJoinedDTO(player);
    }

    @GetMapping("/challenges/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerGetDTO getPlayer(@PathVariable long playerId, @RequestHeader String playerToken) {
        Player player = validatePlayer(playerId, playerToken);
        return DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);
    }

    @PutMapping("/challenges/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerPlayedDTO play(@PathVariable long playerId, @RequestHeader String playerToken, @RequestBody List<Word> words) {
        Player player = validatePlayer(playerId, playerToken);

        Word result = dailyChallengeService.play(player, words);
        PlayerPlayedDTO playerPlayedDTO = DTOMapper.INSTANCE.convertEntityToPlayerPlayedDTO(player);
        playerPlayedDTO.setResultWord(DTOMapper.INSTANCE.convertEntityToWordDTO(result));
        return playerPlayedDTO;
    }

    @DeleteMapping("/challenges/players/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitDailyChallenge(@PathVariable long playerId, @RequestHeader String playerToken) {
        Player player = validatePlayer(playerId, playerToken);

        playerService.deletePlayer(player);
    }

    @GetMapping("/challenges/records")
    @ResponseStatus(HttpStatus.OK)
    public List<DailyChallengeRecordGetDTO> getAllRecordDTOs() {
        List<DailyChallengeRecord> records = dailyChallengeService.getRecords();
        List<DailyChallengeRecordGetDTO> recordDTOs = new ArrayList<>();

        for (DailyChallengeRecord recordItem : records) {
            recordDTOs.add(DTOMapper.INSTANCE.convertEntityToDailyChallengeRecordGetDTO(recordItem));
        }

        return recordDTOs;
    }

    Player validatePlayer(long playerId, String playerToken) {
        if (playerToken == null || playerToken.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player token is empty!");

        Player player = playerService.findPlayerByToken(playerToken);

        if (player.getId() != playerId)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong token for player");

        if (player.getUser() == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No user found for player");

        return player;
    }
}
