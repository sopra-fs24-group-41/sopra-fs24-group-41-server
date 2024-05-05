package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;

import java.util.Set;

public class PlayerPlayedDTO {
    private long points;
    private Set<PlayerWordDTO> playerWords;
    private WordDTO targetWord;
    private WordDTO resultWord;
    private PlayerStatus status;

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

    public WordDTO getResultWord() {
        return resultWord;
    }

    public void setResultWord(WordDTO resultWord) {
        this.resultWord = resultWord;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }
}
