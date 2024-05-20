package ch.uzh.ifi.hase.soprafs24.entity;

import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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

    @Column
    private int depth;

    @Column
    private double reachability;

    @OneToMany(mappedBy = "targetWord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyChallenge> dailyChallenges = new ArrayList<>();

    @Transient
    private boolean newlyDiscovered = false;

    public Word() {
    }

    public Word(String name) {
        setName(name);
        this.depth = 1000;
        this.reachability = 0.0;
    }

    public Word(String name, int depth, double reachability) {
        setName(name);
        this.depth = depth;
        this.reachability = reachability;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Word that = (Word) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name.replaceAll("[^A-Za-z0-9]", "");
        name = name.toLowerCase();
        name = name.trim();
        this.name = name;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public double getReachability() {
        return reachability;
    }

    public void setReachability(double difficultyScore) {
        this.reachability = difficultyScore;
    }

    public List<Combination> getCombinations() {
        return combinations;
    }

    public boolean isNewlyDiscovered() {
        return newlyDiscovered;
    }

    public void setNewlyDiscovered(boolean newlyDiscovered) {
        this.newlyDiscovered = newlyDiscovered;
    }

    public List<DailyChallenge> getDailyChallenges() {
        return dailyChallenges;
    }

    public void setDailyChallenges(List<DailyChallenge> dailyChallenges) {
        this.dailyChallenges = dailyChallenges;
    }
}
