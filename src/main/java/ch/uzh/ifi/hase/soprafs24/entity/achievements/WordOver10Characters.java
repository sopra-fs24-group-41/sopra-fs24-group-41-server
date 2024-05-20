package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class WordOver10Characters extends Achievement {

    public WordOver10Characters() {
        setTitle("Word Architect");
        setDescription("Craft a mega-sized word by joining lots of letters together, turning your word into a skyscraper of language bigger than 10 characters!");
        setProfilePicture("architect");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return combination.getResult().getName().length() > 10;
    }
}
