package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Set;

public class PlayerGetDTO {

    private long id;

    private String name;

    private long points;

    private Set<PlayerWordDTO> playerWords;

    private WordDTO targetWord;

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
}
