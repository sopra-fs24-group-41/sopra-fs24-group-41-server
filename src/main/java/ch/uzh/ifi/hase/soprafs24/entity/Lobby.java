package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Internal Lobby Representation
 * This class composes the internal representation of the lobby and defines how the lobby is stored in the database.
 */
@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private long code;

    @Column
    private String name;

    @Column(nullable = false)
    private Boolean publicAccess;

    @Column
    private LocalDateTime startTime;

    @Column
    private LobbyStatus status;

    @Column
    private GameMode mode;

    @OneToOne(mappedBy = "ownedLobby")
    private Player owner;

    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL)
    private List<Player> players;

    public long getCode() {
    return code;
    }

    public void setCode(long code) {
    this.code = code;
    }

    public String getName() {
    return name;
    }

    public void setName(String name) {
    this.name = name;
    }

    public Boolean getPublicAccess() {
    return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
    this.publicAccess = publicAccess;
    }

    public LocalDateTime getStartTime() {
    return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
    }

    public LobbyStatus getStatus() {
    return status;
    }

    public void setStatus(LobbyStatus status) {
    this.status = status;
    }

    public GameMode getMode() {
    return mode;
    }

    public void setMode(GameMode mode) {
    this.mode = mode;
    }

    public Player getOwner() {
    return owner;
    }

    public void setOwner(Player owner) {
    this.owner = owner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
