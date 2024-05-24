package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class LostFiniteFusionNoUses extends Achievement {

    public LostFiniteFusionNoUses() {
        setTitle("We're all out for today.");
        setDescription("Lose a game of Finite Fusion by running out of elements.");
        setProfilePicture("out_of_stock");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return player.getStatus() == PlayerStatus.LOST
                && player.getLobby().getMode() == GameMode.FINITEFUSION;
    }
}
