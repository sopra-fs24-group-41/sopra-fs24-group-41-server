package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class LostAsOwner extends Achievement {

    public LostAsOwner() {
        setTitle("But it's MY party!");
        setDescription("Lose a game against other players in a lobby you own.");
        setProfilePicture("partydude");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return player.getStatus() == PlayerStatus.LOST
                && player.getLobby().getPlayers().size() >= 2
                && player.getLobby().getOwner() == player;
    }
}
