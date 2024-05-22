package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;

import java.util.Set;

public class PlayerGetDTO {

    private long id;

    private String name;

    private UserGetDTO user;

    private long points;

    private Set<PlayerWordDTO> playerWords;

    private WordDTO targetWord;

    private PlayerStatus status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public Set<PlayerWordDTO> getPlayerWords() {
        return playerWords;
    }

    public void setPlayerWords(Set<PlayerWordDTO> playerWords) {
        this.playerWords = playerWords;
    }

    public WordDTO getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(WordDTO targetWord) {
        this.targetWord = targetWord;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public UserGetDTO getUser() {
        return user;
    }

    public void setUser(UserGetDTO user) {
        this.user = user;
    }
}
