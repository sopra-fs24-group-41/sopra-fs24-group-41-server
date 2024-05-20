package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class MadeFirstCombination extends Achievement {

    public MadeFirstCombination() {
        setTitle("Baby's first combination");
        setDescription("Merge your first combination");
        setProfilePicture("baby");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return true;
    }
}
