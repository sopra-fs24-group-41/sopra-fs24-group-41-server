package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class WonAgainst7 extends Achievement {

    public WonAgainst7() {
        setTitle("Master Merger");
        setDescription("Win a game against at least 7 other players.");
        setProfilePicture("gold");
    }

    boolean unlockConditionFulfilled(Player player, Combination combination) {
        return player.getStatus() == PlayerStatus.WON
                && player.getLobby().getPlayers().size() >= 8;
    }
}
