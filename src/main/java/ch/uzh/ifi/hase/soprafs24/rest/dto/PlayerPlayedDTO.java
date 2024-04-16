package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerWordDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Word;

import java.util.List;

public class PlayerPlayedDTO {
    private long points;
    private List<PlayerWordDTO> playerWords;
    private Word targetWord;

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public List<PlayerWordDTO> getPlayerWords() {
        return playerWords;
    }

    public void setPlayerWords(List<PlayerWordDTO> playerWords) {
        this.playerWords = playerWords;
    }

    public Word getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(Word targetWord) {
        this.targetWord = targetWord;
    }
}
