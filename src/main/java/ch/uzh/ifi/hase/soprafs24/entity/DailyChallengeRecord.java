package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

@Entity
@Table(name = "DAILYCHALLENGE")
@IdClass(DailyChallengeRecordId.class)
public class DailyChallengeRecord {

    @Id
    private Long challengeId;

    @Id
    @OneToOne
    @JoinColumn(name = "user")
    private User user;

    @Column(nullable = false)
    private int numberOfCombinations = 0;

    public Long getChallengeId() { return challengeId; }

    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public int getNumberOfCombinations() { return numberOfCombinations; }

    public void setNumberOfCombinations(int numberOfCombinations) { this.numberOfCombinations = numberOfCombinations; }
}
