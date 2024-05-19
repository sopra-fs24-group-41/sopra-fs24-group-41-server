package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DAILYCHALLENGE")
public class DailyChallenge implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @ManyToOne
    private Word targetWord;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Word getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(Word targetWord) {
        this.targetWord = targetWord;
    }
}
