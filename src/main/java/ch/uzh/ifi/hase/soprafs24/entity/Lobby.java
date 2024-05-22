package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean publicAccess = true;

    @Column(nullable = false)
    private Integer gameTime = 0;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime lastModified = LocalDateTime.now();

    @Column(nullable = false)
    private LobbyStatus status = LobbyStatus.PREGAME;

    @Column
    private GameMode mode = GameMode.STANDARD;

    @OneToOne(mappedBy = "ownedLobby")
    private Player owner;

    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL)
    private List<Player> players;

    @Transient
    private boolean updatedName = false;

    @Transient
    private boolean updatedMode = false;

    @Transient
    private boolean updatedPublicAccess = false;

    @Transient
    private boolean updatedGameTime = false;

    public Lobby() {}

    public Lobby(long code, String name) {
        this.code = code;
        this.name = name;
        this.publicAccess = false;
    }

    @PrePersist
    @PreUpdate
    public void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Lobby that = (Lobby) o;
        return Objects.equals(getCode(), that.getCode()) &&
               Objects.equals(getName(), that.getName()) &&
               Objects.equals(getPublicAccess(), that.getPublicAccess()) &&
               Objects.equals(getStartTime(), that.getStartTime()) &&
               Objects.equals(getStatus(), that.getStatus()) &&
               Objects.equals(getMode(), that.getMode()) &&
               Objects.equals(getOwner(), that.getOwner()) &&
               Objects.equals(getPlayers(), that.getPlayers()) &&
               Objects.equals(getGameTime(), that.getGameTime());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

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
        this.updatedName = true;
        this.name = name;
    }

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.updatedPublicAccess = true;
        this.publicAccess = publicAccess;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
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
        this.updatedMode = true;
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

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void setGameTime(Integer gameTime) {
        this.gameTime = gameTime;
        this.updatedGameTime = true;
    }

    public Integer getGameTime() {
        return gameTime;
    }

    public boolean isUpdatedName() {
        return updatedName;
    }

    public void setUpdatedName(boolean updatedName) {
        this.updatedName = updatedName;
    }

    public boolean isUpdatedMode() {
        return updatedMode;
    }

    public void setUpdatedMode(boolean updatedMode) {
        this.updatedMode = updatedMode;
    }

    public boolean isUpdatedPublicAccess() {
        return updatedPublicAccess;
    }

    public void setUpdatedPublicAccess(boolean updatedPublicAccess) {
        this.updatedPublicAccess = updatedPublicAccess;
    }

    public boolean isUpdatedGameTime() {
        return updatedGameTime;
    }

    public void setUpdatedGameTime(boolean updatedGameTime) {
        this.updatedGameTime = updatedGameTime;
    }

    public void resetUpdate() {
        this.updatedName = false;
        this.updatedMode = false;
        this.updatedPublicAccess = false;
        this.updatedGameTime = false;
    }

    public Map<String, Boolean> getUpdatedFields() {
        return Map.of("name", updatedName, "mode", updatedMode, "publicAccess", updatedPublicAccess, "gameTime", updatedGameTime);
    }
}
