package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class LostAlone extends Achievement {

    public LostAlone() {
        setTitle("I've tried nothing and I'm all out of ideas");
        setDescription("Lose a single player game.");
        setProfilePicture("anonpenguin");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return player.getStatus() == PlayerStatus.LOST
                && player.getLobby().getPlayers().size() == 1;
    }
}
