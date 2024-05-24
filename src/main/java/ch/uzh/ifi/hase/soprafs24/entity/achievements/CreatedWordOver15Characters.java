package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class CreatedWordOver15Characters extends Achievement {

    public CreatedWordOver15Characters() {
        setTitle("Lexical Magician");
        setDescription("Create a word at least 15 characters long.");
        setProfilePicture("wizard");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return combination.getResult().getName().length() >= 15;
    }
}
