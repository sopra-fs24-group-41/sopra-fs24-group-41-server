package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

public class DailyChallengeRecordId implements Serializable {
    private long dailyChallenge;

    private Long user;

    public DailyChallengeRecordId(long dailyChallenge, Long user) {
        this.dailyChallenge = dailyChallenge;
        this.user = user;
    }
}
