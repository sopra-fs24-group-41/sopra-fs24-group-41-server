package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Achievement;

import java.time.LocalDate;
import java.util.Set;

public class UserGetDTO {

    private Long id;
    private String username;
    private UserStatus status;

    private int wins;

    private int losses;

    private LocalDate creationDate;

    private String profilePicture;

    private String favourite;

    private int combinationsMade;

    private int discoveredWords;

    private WordDTO rarestWordFound;

    private Set<Achievement> achievements;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setWins(int wins){this.wins = wins;}

    public int getWins(){return this.wins;}

    public void setLosses(int losses){this.losses = losses;}

    public int getLosses(){return this.losses;}

    public LocalDate getCreationDate() {return creationDate;}

    public void setCreationDate(LocalDate creationDate) {this.creationDate = creationDate;}

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public int getCombinationsMade() {
        return combinationsMade;
    }

    public void setCombinationsMade(int combinationsMade) {
        this.combinationsMade = combinationsMade;
    }

    public int getDiscoveredWords() {
        return discoveredWords;
    }

    public void setDiscoveredWords(int discoveredWords) {
        this.discoveredWords = discoveredWords;
    }

    public WordDTO getRarestWordFound() {
        return rarestWordFound;
    }

    public void setRarestWordFound(WordDTO rarestFoundWord) {
        this.rarestWordFound = rarestFoundWord;
    }

    public Set<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(Set<Achievement> achievements) {
        this.achievements = achievements;
    }
}
