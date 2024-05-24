package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Entity
public class SpentTimeInSandbox extends Achievement {

    public SpentTimeInSandbox() {
        setTitle("Building sandcastles by myself");
        setDescription("Spend at least 10 minutes alone playing in sandbox mode.");
        setProfilePicture("sandcastle_v2");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        Lobby lobby = player.getLobby();
        long minutes = ChronoUnit.MINUTES.between(lobby.getStartTime(), LocalDateTime.now());

        return minutes >= 10
                && lobby.getPlayers().size() == 1
                && lobby.getMode() == GameMode.STANDARD;
    }
}
