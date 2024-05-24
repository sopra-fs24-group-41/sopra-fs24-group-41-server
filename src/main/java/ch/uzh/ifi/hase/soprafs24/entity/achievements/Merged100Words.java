package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class Merged100Words extends Achievement {

    public Merged100Words() {
        setTitle("Womboed 100 Combos");
        setDescription("Make 100 combinations.");
        setProfilePicture("wombo_combo_one_hundred");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return player.getUser().getCombinationsMade() > 100;
    }
}
