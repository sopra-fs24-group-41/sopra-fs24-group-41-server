package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Internal Player Representation
 * This class composes the internal representation of the player and defines how the player is stored in the database.
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
    private String name;

    @Column
    private long points;

    @OneToOne(mappedBy = "player")
    private User user;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<PlayerWord> availableWords;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ownedLobby")
    private Lobby ownedLobby;

    @ManyToOne
    @JoinColumn(name = "lobby")
    private Lobby lobby;

    public Player() {}

    public Player(String token, String name, Lobby lobby) {
        this.token = token;
        this.name = name;
        this.lobby = lobby;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public List<PlayerWord> getAvailableWords() {
        return availableWords;
    }

    public void setAvailableWords(List<PlayerWord> availableWords) {
        this.availableWords = availableWords;
    }

    public Lobby getOwnedLobby() {
        return ownedLobby;
    }

    public void setOwnedLobby(Lobby ownsLobby) {
        this.ownedLobby = ownsLobby;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
