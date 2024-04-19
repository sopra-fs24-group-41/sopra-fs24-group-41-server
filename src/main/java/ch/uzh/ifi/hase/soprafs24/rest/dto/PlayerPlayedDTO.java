package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerWordDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Word;

import java.util.List;
import java.util.Set;

public class PlayerPlayedDTO {
    private long points;
    private Set<PlayerWordDTO> playerWords;
    private WordDTO targetWord;

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
