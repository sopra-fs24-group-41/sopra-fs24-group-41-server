package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class CreatedWordOver10Characters extends Achievement {

    public CreatedWordOver10Characters() {
        setTitle("Word Architect");
        setDescription("Create a word at least 10 characters long.");
        setProfilePicture("architect");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return combination.getResult().getName().length() >= 10;
    }
}
