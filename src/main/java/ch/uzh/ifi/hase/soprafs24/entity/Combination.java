package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "COMBINATION", uniqueConstraints = {@UniqueConstraint(columnNames = {"word1", "word2"})})
public class Combination implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "word1", nullable = false)
    private Word word1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "word2", nullable = false)
    private Word word2;

    @ManyToOne(optional = false)
    @JoinColumn(name = "result", nullable = false)
    private Word result;

    public Combination() {
    }

    public Combination(Word word1, Word word2, Word result) {
        this.word1 = word1;
        this.word2 = word2;
        this.result = result;
    }

    public Long getId() {
        return id;
    }

    public Word getResult() {
        return result;
    }

    public void setResult(Word result) {
        this.result = result;
    }

    public Word getWord1() {
        return word1;
    }

    public void setWord1(Word word1) {
        this.word1 = word1;
    }

    public Word getWord2() {
        return word2;
    }

    public void setWord2(Word word2) {
        this.word2 = word2;
    }
}
