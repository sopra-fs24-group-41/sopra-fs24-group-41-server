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
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "word")
    private Word word;

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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PlayerWord that = (PlayerWord) o;
        return Objects.equals(getPlayer(), that.getPlayer()) &&
               Objects.equals(getWord(), that.getWord());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
