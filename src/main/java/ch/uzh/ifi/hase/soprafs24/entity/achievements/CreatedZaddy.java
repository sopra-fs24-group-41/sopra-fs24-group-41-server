package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class CreatedZaddy extends Achievement {

    public CreatedZaddy() {
        setTitle("...how?");
        setDescription("Create zaddy through any combination.");
        setProfilePicture("zaddy");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return combination.getResult().getName().equals("zaddy");
    }
}
