package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class CreatedLife extends Achievement {

    public CreatedLife() {
        setTitle("Life, uh, finds a way.");
        setDescription("Create life through any combination.");
        setProfilePicture("goldblum");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return combination.getResult().getName().equals("life");
    }
}
