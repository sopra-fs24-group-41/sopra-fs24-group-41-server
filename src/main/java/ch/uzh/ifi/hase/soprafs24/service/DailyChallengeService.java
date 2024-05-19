package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.DailyChallenge;
import ch.uzh.ifi.hase.soprafs24.repository.DailyChallengeRecordRepository;
import ch.uzh.ifi.hase.soprafs24.repository.DailyChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class DailyChallengeService {
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final DailyChallengeRepository dailyChallengeRepository;

    private final DailyChallengeRecordRepository dailyChallengeRecordRepository;

    @Autowired
    public DailyChallengeService(@Qualifier("dailyChallengeRepository") DailyChallengeRepository dailyChallengeRepository,
                                 @Qualifier("dailyChallengeRecordRepository") DailyChallengeRecordRepository dailyChallengeRecordRepository) {
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.dailyChallengeRecordRepository = dailyChallengeRecordRepository;
    }

    @Scheduled(cron = "* * * * * *")
    public void createNewDailyChallenge() {
        System.out.println("test123");
    }

    // functions:
    // insert user with attempts
    // play (like in GameService)
    // reset with sch. task

}
