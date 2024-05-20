package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.DailyChallengeRecordRepository;
import ch.uzh.ifi.hase.soprafs24.repository.DailyChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.min;

@Service
@Transactional
public class DailyChallengeService {
    private final DailyChallengeRepository dailyChallengeRepository;

    private final DailyChallengeRecordRepository dailyChallengeRecordRepository;

    private final WordService wordService;

    @Autowired
    public DailyChallengeService(@Qualifier("dailyChallengeRepository") DailyChallengeRepository dailyChallengeRepository,
                                 @Qualifier("dailyChallengeRecordRepository") DailyChallengeRecordRepository dailyChallengeRecordRepository,
                                 WordService wordService) {
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.dailyChallengeRecordRepository = dailyChallengeRecordRepository;
        this.wordService = wordService;
    }

    public DailyChallengeRecord getDailyChallengeRecord(DailyChallengeRecord recordItem) {
        Optional<DailyChallengeRecord> foundRecord = dailyChallengeRecordRepository
                .findById(new DailyChallengeRecordId(
                        recordItem.getDailyChallenge().getId(),
                        recordItem.getUser().getId()));

        return foundRecord.orElseGet(() -> dailyChallengeRecordRepository.saveAndFlush(recordItem));
    }

    public List<DailyChallengeRecord> getRecords() {
        return dailyChallengeRecordRepository.findAll();
    }

//    @Scheduled(cron = "0 * * * * *")
    @EventListener(ApplicationReadyEvent.class)
    void createNewDailyChallenge() {
        dailyChallengeRecordRepository.deleteAll();
        dailyChallengeRepository.deleteAll();

        DailyChallenge dailyChallenge = new DailyChallenge();
        dailyChallenge.setTargetWord(wordService.getRandomWordWithinReachability(0.1, 0.4));
        dailyChallengeRepository.saveAndFlush(dailyChallenge);
    }

    public DailyChallenge getDailyChallenge() { return dailyChallengeRepository.findAll().get(0); }

    public Word getTargetWord() {
        return getDailyChallenge().getTargetWord();
    }

    public void updateRecords(Lobby lobby) {
        DailyChallenge dailyChallenge = getDailyChallenge();

        for (Player player : lobby.getPlayers()) {
            DailyChallengeRecord dailyChallengeRecord = getDailyChallengeRecord(new DailyChallengeRecord(dailyChallenge, player.getUser(), player.getPoints()));
            dailyChallengeRecord.setNumberOfCombinations(min(dailyChallengeRecord.getNumberOfCombinations(), player.getPoints()));
        }
    }
}
