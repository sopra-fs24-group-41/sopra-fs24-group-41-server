package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class WonWomboComboWithoutTarget extends Achievement {

    public WonWomboComboWithoutTarget() {
        setTitle("I won... I guess?");
        setDescription("Win a game of Wombo Combo without actually reaching a target word.");
        setProfilePicture("pikachu");
        setHidden(true);
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        int mergedWords = player.getPlayerWords().size() - 4;
        return player.getStatus() == PlayerStatus.WON
                && player.getLobby().getMode() == GameMode.WOMBOCOMBO
                && mergedWords == player.getPoints();
    }
}
