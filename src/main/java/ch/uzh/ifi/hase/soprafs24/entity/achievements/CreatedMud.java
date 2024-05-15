package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.*;


@Entity
public class CreatedMud extends Achievement {

    public CreatedMud() {
        setTitle("This stuff is everywhere!");
        setDescription("Create mud through any combination.");
        setProfilePicture("mud.png");
    }

    public void unlock(Player player, Combination combination) {
        if (combination.getResult().getName().equals("mud")) {
            player.getUser().addAchievement(this);
        }
    }
}
