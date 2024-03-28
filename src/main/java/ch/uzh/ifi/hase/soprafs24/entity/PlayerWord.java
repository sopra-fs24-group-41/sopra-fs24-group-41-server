package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Internal PlayerWord Representation
 * This class composes the internal representation of the playerWords and defines how
 * the playerWords are stored in the database.
 */
@Entity
@Table(name = "PLAYERWORDS")
@IdClass(PlayerWordId.class)
public class PlayerWord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "player")
    private long playerId;

    @Id
    @Column(name = "word")
    private long wordId;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "player")
    private Player player;

    @OneToOne
    @MapsId
    @JoinColumn(name = "word")
    private Word word;

    @Column
    private LocalDateTime timestamp;

    @PrePersist
    void timestamp() {
        this.timestamp = LocalDateTime.now();
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getWordId() {
        return wordId;
    }

    public void setWordId(long wordId) {
        this.wordId = wordId;
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
