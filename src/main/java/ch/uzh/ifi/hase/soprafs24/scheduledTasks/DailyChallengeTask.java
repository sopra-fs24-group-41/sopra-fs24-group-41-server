package ch.uzh.ifi.hase.soprafs24.scheduledTasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyChallengeTask {

    @Scheduled(cron = "0 0 0 * * *")
    public void createNewDailyChallenge() {
        System.out.println("test123");
    }
}
