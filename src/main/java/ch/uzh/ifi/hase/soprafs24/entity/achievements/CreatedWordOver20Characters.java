package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class CreatedWordOver20Characters extends Achievement {

    public CreatedWordOver20Characters() {
        setTitle("Supercalifragilisticexpialidocious");
        setDescription("Create a word at least 20 characters long.");
        setProfilePicture("mary_poppins");
        setHidden(true);
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return combination.getResult().getName().length() >= 20;
    }
}
