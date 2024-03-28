package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Internal Lobby Representation
 * This class composes the internal representation of the lobby and defines how
 * the lobby is stored in the database.
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "PLAYER",
          joinColumns = { @JoinColumn(name = "lobbyCode", referencedColumnName = "code") },
          inverseJoinColumns = { @JoinColumn(name = "playerId", referencedColumnName = "id") }
    )
    private Player master;

    @OneToMany(mappedBy = "id")
    private Collection<Player> players;

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

    public Player getMaster() {
    return master;
    }

    public void setMaster(Player master) {
    this.master = master;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Collection<Player> players) {
        this.players = players;
    }
}
