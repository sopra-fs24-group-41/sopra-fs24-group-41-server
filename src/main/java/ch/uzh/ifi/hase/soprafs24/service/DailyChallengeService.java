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

    public DailyChallengeRecord getDailyChallengeRecord(DailyChallenge dailyChallenge, User user) {
        Optional<DailyChallengeRecord> foundRecord = findRecord(dailyChallenge, user);

        return foundRecord.orElseGet(() -> saveRecord(dailyChallenge, user));
    }

    private DailyChallengeRecord saveRecord(DailyChallenge dailyChallenge, User user) {
        return dailyChallengeRecordRepository.saveAndFlush(new DailyChallengeRecord(dailyChallenge, user));
    }

    public Optional<DailyChallengeRecord> findRecord(DailyChallenge dailyChallenge, User user) {
        return dailyChallengeRecordRepository.findById(new DailyChallengeRecordId(dailyChallenge.getId(), user.getId()));
    }
    public List<DailyChallengeRecord> getRecords() {
        return dailyChallengeRecordRepository.findAll();
    }

    void createNewDailyChallenge() {
        DailyChallenge dailyChallenge = new DailyChallenge();
        dailyChallenge.setTargetWord(wordService.selectTargetWord(0.001, 0.03));
        dailyChallengeRepository.saveAndFlush(dailyChallenge);
    }

    @EventListener(ApplicationReadyEvent.class)
    void setUpDailyChallenge() {
        if (dailyChallengeRepository.count() == 0) {
            createNewDailyChallenge();
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    void updateDailyChallenge() {
        if (dailyChallengeRepository.count() != 0) {
            dailyChallengeRecordRepository.deleteAll();
            dailyChallengeRepository.deleteAll();
        }

        createNewDailyChallenge();
    }


    public DailyChallenge getDailyChallenge() { return dailyChallengeRepository.findAll().get(0); }

    public Word getTargetWord() {
        return getDailyChallenge().getTargetWord();
    }

    public void updateRecords(Lobby lobby) {
        DailyChallenge dailyChallenge = getDailyChallenge();

        for (Player player : lobby.getPlayers()) {
            if (player.getUser() != null) {
                DailyChallengeRecord dailyChallengeRecord = getDailyChallengeRecord(dailyChallenge, player.getUser());
                dailyChallengeRecord.setNumberOfCombinations(min(dailyChallengeRecord.getNumberOfCombinations(), player.getPoints()));
            }
        }
    }
}
