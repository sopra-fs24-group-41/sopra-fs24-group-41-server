package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.game.DailyChallengeGame;
import ch.uzh.ifi.hase.soprafs24.repository.DailyChallengeRecordRepository;
import ch.uzh.ifi.hase.soprafs24.repository.DailyChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Math.min;

@Service
@Transactional
public class DailyChallengeService {
    private final Logger log = LoggerFactory.getLogger(DailyChallengeService.class);

    private final DailyChallengeRepository dailyChallengeRepository;

    private final DailyChallengeRecordRepository dailyChallengeRecordRepository;

    private final PlayerService playerService;
    private final CombinationService combinationService;
    private final WordService wordService;

    @Autowired
    public DailyChallengeService(@Qualifier("dailyChallengeRepository") DailyChallengeRepository dailyChallengeRepository,
                                 @Qualifier("dailyChallengeRecordRepository") DailyChallengeRecordRepository dailyChallengeRecordRepository,
                                 PlayerService playerService,
                                 CombinationService combinationService,
                                 WordService wordService) {
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.dailyChallengeRecordRepository = dailyChallengeRecordRepository;
        this.playerService = playerService;
        this.combinationService = combinationService;
        this.wordService = wordService;
    }

    public DailyChallengeRecord getDailyChallengeRecord(DailyChallengeRecord record) {
        Optional<DailyChallengeRecord> foundRecord = dailyChallengeRecordRepository
                .findById(new DailyChallengeRecordId(
                        record.getDailyChallenge().getId(),
                        record.getUser().getId()));

        return foundRecord.orElseGet(() -> dailyChallengeRecordRepository.saveAndFlush(record));
    }

    @Scheduled(cron = "* * * * * *")
    public void createNewDailyChallenge() {
        System.out.println("test123");
    }

    public Player startGame(User user) {
        Player player = new Player(UUID.randomUUID().toString(), user.getUsername(), null);

        Word targetWord = getTargetWord();
        DailyChallengeGame game = new DailyChallengeGame(playerService, combinationService, wordService, targetWord);
        game.setUpPlayer(player);
        return player;
    }

    public Word getTargetWord() {
        return dailyChallengeRepository.findAll().get(0).getTargetWord();
    }

    public Word play(Player player, List<Word> words) {
        Word targetWord = getTargetWord();
        DailyChallengeGame game = new DailyChallengeGame(playerService, combinationService, wordService, targetWord);
        Word result = game.makeCombination(player, words);

        if (game.winConditionReached(player)) {
            endGame(player);
        }
        return result;
    }

    void endGame(Player player) {
        DailyChallenge dailyChallenge = dailyChallengeRepository.findAll().get(0);

        DailyChallengeRecord dailyChallengeRecord = getDailyChallengeRecord(new DailyChallengeRecord(dailyChallenge, player.getUser(), player.getPoints()));
        dailyChallengeRecord.setNumberOfCombinations(min(dailyChallengeRecord.getNumberOfCombinations(), player.getPoints()));
    }

    // functions:
    // initialize game?
    // insert user with attempts -- endGame
    // play (like in GameService)
    // reset with sch. task

}
