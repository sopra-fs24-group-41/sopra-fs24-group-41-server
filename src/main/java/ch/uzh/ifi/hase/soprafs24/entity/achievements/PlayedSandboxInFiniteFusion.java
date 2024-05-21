package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class PlayedSandboxInFiniteFusion extends Achievement {

    public PlayedSandboxInFiniteFusion() {
        setTitle("Never give up!");
        setDescription("Make at least 5 new words while having already lost Finite Fusion.");
        setProfilePicture("coolpenguin");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return player.getPoints() >= 5
                && player.getStatus() == PlayerStatus.LOST
                && player.getLobby().getMode() == GameMode.FINITEFUSION;
    }
}
