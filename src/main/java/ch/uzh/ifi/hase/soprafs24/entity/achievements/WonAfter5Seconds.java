package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Entity
public class WonAfter5Seconds extends Achievement {

    public WonAfter5Seconds() {
        setTitle("I am speed.");
        setDescription("Win a game within 5 seconds.");
        setProfilePicture("speed");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        Lobby lobby = player.getLobby();
        long elapsedSeconds = ChronoUnit.SECONDS.between(lobby.getStartTime(), LocalDateTime.now());
        return player.getStatus() == PlayerStatus.WON
                && elapsedSeconds <= 5;
    }
}
