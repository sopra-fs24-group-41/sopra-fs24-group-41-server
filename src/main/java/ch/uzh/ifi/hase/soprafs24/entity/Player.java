package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

/**
 * Internal Player Representation
 * This class composes the internal representation of the player and defines how
 * the player is stored in the database.
 */
@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String username;

    @Column
    private long points;

    @OneToMany(mappedBy = "player")
    private Collection<PlayerWord> availableWords;

    @OneToOne(mappedBy = "master")
    private Lobby ownsLobby;

    @ManyToOne
    @JoinColumn(name = "code")
    private Lobby inLobby;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public Collection<PlayerWord> getAvailableWords() {
        return availableWords;
    }

    public void setAvailableWords(Collection<PlayerWord> availableWords) {
        this.availableWords = availableWords;
    }

    public Lobby getOwnsLobby() {
        return ownsLobby;
    }

    public void setOwnsLobby(Lobby ownsLobby) {
        this.ownsLobby = ownsLobby;
    }

    public Lobby getInLobby() {
        return inLobby;
    }

    public void setInLobby(Lobby inLobby) {
        this.inLobby = inLobby;
    }
}
