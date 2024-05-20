package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;


@Entity
public class MadeNoNewWords extends Achievement {

    public MadeNoNewWords() {
        setTitle("Whole lot of nothing");
        setDescription("Play a game with other people, make at least one combination, and loose without adding any new words to the word board.");
        setProfilePicture("platypus");
        setHidden(true);
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        return player.getLobby().getPlayers().size() >= 2
                && player.getPlayerWords().size() == 4
                && player.getStatus() == PlayerStatus.LOST;
    }
}
