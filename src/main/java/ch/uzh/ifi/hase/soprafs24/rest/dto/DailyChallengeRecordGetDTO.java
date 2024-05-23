package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class DailyChallengeRecordGetDTO {
    private UserGetDTO user;

    private long numberOfCombinations;

    public UserGetDTO getUser() {
        return user;
    }

    public void setUser(UserGetDTO user) {
        this.user = user;
    }

    public long getNumberOfCombinations() {
        return numberOfCombinations;
    }

    public void setNumberOfCombinations(long numberOfCombinations) {
        this.numberOfCombinations = numberOfCombinations;
    }
}
