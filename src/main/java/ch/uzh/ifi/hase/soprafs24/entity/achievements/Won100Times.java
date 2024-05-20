package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class Won100Times extends Achievement {

    public Won100Times() {
        setTitle("Time well spent");
        setDescription("Win 100 games.");
        setProfilePicture("galaxy_gorilla");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return player.getUser().getWins() >= 100;
    }
}
