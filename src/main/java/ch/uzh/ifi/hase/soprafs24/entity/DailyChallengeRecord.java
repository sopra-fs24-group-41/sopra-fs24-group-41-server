package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

@Entity
@Table(name = "DAILYCHALLENGERECORD")
@IdClass(DailyChallengeRecordId.class)
public class DailyChallengeRecord {

    @Id
    @ManyToOne
    @JoinColumn(name="dailychallenge")
    private DailyChallenge dailyChallenge;

    @Id
    @ManyToOne
    @JoinColumn(name="users")
    private User user;

    @Column(nullable = false)
    private int numberOfCombinations = 0;

    public DailyChallenge dailyChallenge() { return dailyChallenge; }

    public void setChallengeId(DailyChallenge dailyChallenge) { this.dailyChallenge = dailyChallenge; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public int getNumberOfCombinations() { return numberOfCombinations; }

    public void setNumberOfCombinations(int numberOfCombinations) { this.numberOfCombinations = numberOfCombinations; }
}
