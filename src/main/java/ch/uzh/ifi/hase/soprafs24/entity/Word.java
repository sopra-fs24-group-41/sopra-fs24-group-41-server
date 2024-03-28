package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "WORD")
public class Word implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column()
    private String name;

    @OneToMany(mappedBy = "result")
    private List<Combination> combinations;

    public Word() {
    }

    public Word(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Combination> getCombinations() {
        return combinations;
    }
}
