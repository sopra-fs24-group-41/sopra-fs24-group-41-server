package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class Discovered20Words extends Achievement {

    public Discovered20Words() {
        setTitle("One small step for man - one giant leap for Wombo");
        setDescription("Discover 20 new words.");
        setProfilePicture("moon");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return player.getUser().getDiscoveredWords() > 20;
    }
}
