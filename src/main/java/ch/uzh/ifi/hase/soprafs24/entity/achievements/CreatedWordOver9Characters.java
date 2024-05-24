package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class CreatedWordOver9Characters extends Achievement {

    public CreatedWordOver9Characters() {
        setTitle("Lexical Magician");
        setDescription("Step into the spotlight as a wizard of words, conjuring a spellbinding creation exceeding 20 characters.");
        setProfilePicture("gandalf");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return combination.getResult().getName().length() > 9;
    }
}
