package ch.uzh.ifi.hase.soprafs24.entity;

import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Internal PlayerWord Representation
 * This class composes the internal representation of the playerWords and defines how they are stored in the database.
 */
@Entity
@Table(name = "PLAYERWORDS")
@IdClass(PlayerWordId.class)
public class PlayerWord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "player")
    private Player player;

    @Id
    @ManyToOne()
    @JoinColumn(name = "word")
    private Word word;

    @Column
    private Integer uses;

    @Column
    private LocalDateTime timestamp;

    @PrePersist
    void timestamp() {
        this.timestamp = LocalDateTime.now();
    }

    public PlayerWord() {
    }

    public PlayerWord(Player player, Word word) {
        this.player = player;
        this.word = word;
    }

    public PlayerWord(Player player, Word word, int uses) {
        this.player = player;
        this.word = word;
        this.uses = uses;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PlayerWord that = (PlayerWord) o;
        return Objects.equals(getPlayer(), that.getPlayer()) &&
               Objects.equals(getWord(), that.getWord());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Integer getUses() {
        return uses;
    }

    public void setUses(Integer uses) {
        this.uses = uses;
    }

    public void addUses(int uses) {
        this.uses += uses;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
