package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class WordOver34Characters extends Achievement {

    public WordOver34Characters() {
        setTitle("Supercalifragilisticexpialidocious");
        setDescription("Unlock this achievement by creating a word even longer than Mary Poppins' favorite. That's right, a word longer than 34 characters!");
        setProfilePicture("marypoppins");
        setHidden(true);
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return combination.getResult().getName().length() > 34;
    }
}
