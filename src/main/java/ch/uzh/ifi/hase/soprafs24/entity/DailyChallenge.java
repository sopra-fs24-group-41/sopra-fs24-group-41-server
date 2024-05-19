package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DAILYCHALLENGE")
public class DailyChallenge implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private Word targetWord;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Word getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(Word targetWord) {
        this.targetWord = targetWord;
    }
}
