package ch.uzh.ifi.hase.soprafs24.entity;

import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;

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
    private long points = 0;

    @OneToOne(mappedBy = "player")
    private User user;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlayerWord> playerWords = new HashSet<PlayerWord>();

    @ManyToOne
    private Word targetWord;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ownedLobby")
    private Lobby ownedLobby;

    @ManyToOne
    @JoinColumn(name = "lobby")
    private Lobby lobby;

    public Player() {
    }

    public Player(String token, String name, Lobby lobby) {
        this.token = token;
        this.name = name;
        this.lobby = lobby;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Player that = (Player) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getToken(), that.getToken()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getPoints(), that.getPoints()) &&
                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getWords(), that.getWords()) &&
                Objects.equals(getTargetWord(), that.getTargetWord()) &&
                Objects.equals(getOwnedLobby(), that.getOwnedLobby()) &&
                Objects.equals(getLobby(), that.getLobby());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
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

    public void addPoints(long points) {
        this.points += points;
    }

    public Set<PlayerWord> getPlayerWords() {
        return playerWords;
    }

    public void clearPlayerWords() {
        playerWords.clear();
    }

    public List<Word> getWords() {
        return playerWords.stream().map(PlayerWord::getWord).toList();
    }

    public void addWords(List<Word> words) {
        playerWords.addAll(words.stream().map(word -> new PlayerWord(this, word)).collect(Collectors.toSet()));
    }

    public void addWord(Word word) {
        playerWords.add(new PlayerWord(this, word));
    }

    public Word getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(Word targetWord) {
        this.targetWord = targetWord;
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

    public void addWinsToUser(int wins) {
        if (this.getUser() != null) {
            this.getUser().addWins(wins);
        }
    }

    public void addLossesToUser(int losses) {
        if (this.getUser() != null) {
            this.getUser().addLosses(losses);
        }
    }
}
