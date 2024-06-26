package ch.uzh.ifi.hase.soprafs24.entity;

import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "DAILYCHALLENGERECORD")
@IdClass(DailyChallengeRecordId.class)
public class DailyChallengeRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name="daily_challenge")
    private DailyChallenge dailyChallenge;

    @Id
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable = false)
    private long numberOfCombinations = 10000;

    public DailyChallengeRecord() {

    }

    public DailyChallengeRecord(DailyChallenge dailyChallenge, User user) {
        this.dailyChallenge = dailyChallenge;
        this.user = user;
    }

    public DailyChallengeRecord(DailyChallenge dailyChallenge, User user, long numberOfCombinations) {
        this.dailyChallenge = dailyChallenge;
        this.user = user;
        this.numberOfCombinations = numberOfCombinations;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        DailyChallengeRecord that = (DailyChallengeRecord) o;
        return Objects.equals(getDailyChallenge(), that.getDailyChallenge()) &&
                Objects.equals(getUser(), that.getUser());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public DailyChallenge getDailyChallenge() { return dailyChallenge; }

    public void setChallenge(DailyChallenge dailyChallenge) { this.dailyChallenge = dailyChallenge; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public long getNumberOfCombinations() { return numberOfCombinations; }

    public void setNumberOfCombinations(long numberOfCombinations) { this.numberOfCombinations = numberOfCombinations; }
}
