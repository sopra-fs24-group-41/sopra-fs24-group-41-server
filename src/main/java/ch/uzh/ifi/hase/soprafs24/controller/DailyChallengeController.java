package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.DailyChallengeRecord;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DailyChallengeRecordGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPlayedDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.WordDTO;
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

    @PostMapping("/challenges")
    @ResponseStatus(HttpStatus.OK)
    public PlayerGetDTO startChallenge(@RequestHeader String userToken) {
        if (userToken == null || userToken.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User token is empty!");

        User user = userService.checkToken(userToken);
        Player player = dailyChallengeService.startGame(user);

        return DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);
    }

    @PutMapping("/challenges")
    @ResponseStatus(HttpStatus.OK)
    public PlayerPlayedDTO play(@RequestHeader String userToken, @RequestBody List<Word> words) {
        if (userToken == null || userToken.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User token is empty!");

        User user = userService.checkToken(userToken);

        Word result = dailyChallengeService.play(user.getPlayer(), words);
        PlayerPlayedDTO playerPlayedDTO = DTOMapper.INSTANCE.convertEntityToPlayerPlayedDTO(user.getPlayer());
        playerPlayedDTO.setResultWord(DTOMapper.INSTANCE.convertEntityToWordDTO(result));
        return playerPlayedDTO;
    }

    @GetMapping("/challenges/records")
    @ResponseStatus(HttpStatus.OK)
    public List<DailyChallengeRecordGetDTO> getAllRecordDTOs() {
        List<DailyChallengeRecord> records = dailyChallengeService.getRecords();
        List<DailyChallengeRecordGetDTO> recordDTOs = new ArrayList<>();

        for (DailyChallengeRecord record : records) {
            recordDTOs.add(DTOMapper.INSTANCE.convertEntityToDailyChallengeRecordGetDTO(record));
        }

        return recordDTOs;
    }

    @
}
