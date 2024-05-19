package ch.uzh.ifi.hase.soprafs24.entity;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "DAILYCHALLENGERECORD")
@IdClass(DailyChallengeRecordId.class)
public class DailyChallengeRecord {

    @Id
    @ManyToOne
    @JoinColumn(name="dailychallenge")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private DailyChallenge dailyChallenge;

    @Id
    @ManyToOne
    @JoinColumn(name="users")
    private User user;

    @Column(nullable = false)
    private long numberOfCombinations = 10000;

    public DailyChallengeRecord(DailyChallenge dailyChallenge, User user, long numberOfCombinations) {
        this.dailyChallenge = dailyChallenge;
        this.user = user;
        this.numberOfCombinations = numberOfCombinations;
    }

    public DailyChallengeRecord() {

    }

    public DailyChallenge getDailyChallenge() { return dailyChallenge; }

    public void setChallenge(DailyChallenge dailyChallenge) { this.dailyChallenge = dailyChallenge; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public long getNumberOfCombinations() { return numberOfCombinations; }

    public void setNumberOfCombinations(long numberOfCombinations) { this.numberOfCombinations = numberOfCombinations; }
}
