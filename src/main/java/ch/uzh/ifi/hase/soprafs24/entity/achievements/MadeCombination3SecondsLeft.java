package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Entity
public class MadeCombination3SecondsLeft extends Achievement {

    public MadeCombination3SecondsLeft() {
        setTitle("Just. One. More!");
        setDescription("Make a combination with less than 3 seconds remaining.");
        setProfilePicture("cat");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        Lobby lobby = player.getLobby();
        Integer gameTime = player.getLobby().getGameTime();
        if (gameTime <= 0) return false;
        long elapsedSeconds = ChronoUnit.SECONDS.between(lobby.getStartTime(), LocalDateTime.now());
        return gameTime - elapsedSeconds <= 3;
    }
}
