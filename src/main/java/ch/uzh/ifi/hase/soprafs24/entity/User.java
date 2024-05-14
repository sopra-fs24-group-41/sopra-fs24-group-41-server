package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USERS")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String favourite;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int wins = 0;

    @Column(nullable = false)
    private int losses = 0;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private UserStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "playerId")
    private Player player;

    @Column
    private String profilePicture;

    @Column(nullable = false, updatable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    private int combinationsMade = 0;

    // Number of new words that have been added to the word database as a result of combinations made by player.
    @Column(nullable = false)
    private int discoveredWords = 0;

    @ManyToOne
    private Word rarestWordFound = null;

    @ManyToMany
    private Set<Achievement> achievements = new HashSet<Achievement>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getWins() {
        return this.wins;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void addWins(int wins) {
        this.wins += wins;
    }

    public void addLosses(int losses) {
        this.losses += losses;
    }

    public int getLosses() {
        return this.losses;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public int getCombinationsMade() {
        return combinationsMade;
    }

    public void setCombinationsMade(int combinationsMade) {
        this.combinationsMade = combinationsMade;
    }

    public int getDiscoveredWords() {
        return discoveredWords;
    }

    public void setDiscoveredWords(int discoveredWords) {
        this.discoveredWords = discoveredWords;
    }

    public Word getRarestWordFound() {
        return rarestWordFound;
    }

    public void setRarestWordFound(Word rarestFoundWord) {
        this.rarestWordFound = rarestFoundWord;
    }
    public void setCreationDate(LocalDate creationDate) {this.creationDate = creationDate;}

    public Set<Achievement> getAchievements() {
        return new HashSet<Achievement>(this.achievements);
    }

    public void addAchievement (Achievement achievement) {
        this.achievements.add(achievement);
    }
}
