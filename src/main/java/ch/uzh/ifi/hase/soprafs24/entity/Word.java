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
    private List<Combination> combinations = new ArrayList<>();

    @Column
    private Integer depth;

    @Column
    private Double reachability;

    @OneToMany(mappedBy = "targetWord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyChallenge> dailyChallenges = new ArrayList<>();

    @Transient
    private boolean newlyDiscovered = false;

    public Word() {
    }

    public Word(String name) {
        setName(name);
    }

    public Word(String name, Integer depth) {
        setName(name);
        this.depth = depth;
    }

    public Word(String name, Integer depth, Double reachability) {
        setName(name);
        this.depth = depth;
        this.reachability = reachability;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Word that = (Word) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name.replaceAll("[^ A-Za-z0-9]", "");
        name = name.toLowerCase();
        name = name.trim();
        this.name = name;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public void updateDepth(int depth1, int depth2) {
        int newDepth = Math.max(depth1, depth2) + 1;
        if (depth == null) {
            depth = newDepth;
        }
        else {
            depth = Math.min(depth, newDepth);
        }
    }

    public Double getReachability() {
        return reachability;
    }

    public void setReachability(Double reachability) {
        this.reachability = reachability;
    }

    public List<Combination> getCombinations() {
        return List.copyOf(combinations);
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

    public void updateReachability() {
        if (depth == 0) {
            reachability = null;
            return;
        }

        double newReachability = 1.0 / (1L << depth);

        if (reachability == null) {
            reachability = newReachability;
        }
        else {
            reachability += newReachability;
        }

    }
}
