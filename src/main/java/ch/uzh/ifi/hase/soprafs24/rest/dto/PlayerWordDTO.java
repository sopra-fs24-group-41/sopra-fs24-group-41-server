package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDateTime;

public class PlayerWordDTO {
    private WordDTO word;

    private Integer uses;

    private LocalDateTime timestamp;

    private boolean newlyDiscovered;

    public WordDTO getWord() {
        return word;
    }

    public void setWord(WordDTO word) {
        this.word = word;
    }

    public Integer getUses() {
        return uses;
    }

    public void setUses(Integer uses) {
        this.uses = uses;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isNewlyDiscovered() {
        return newlyDiscovered;
    }

    public void setNewlyDiscovered(boolean newlyDiscovered) {
        this.newlyDiscovered = newlyDiscovered;
    }
}
