package ch.uzh.ifi.hase.soprafs24.entity.achievements;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import javax.persistence.Entity;
import java.util.List;


@Entity
public class WonBeforeOtherPlayersPlayed extends Achievement {

    public WonBeforeOtherPlayersPlayed() {
        setTitle("They didn't stand a chance");
        setDescription("Win a game against at least 2 players before either of them could make a new word.");
        setProfilePicture("flash_dc");
    }

    public boolean unlockConditionFulfilled(Player player, Combination combination) {
        List<Player> players = player.getLobby().getPlayers();
        for (Player otherPlayer : players) {
            if (otherPlayer != player && otherPlayer.getPlayerWords().size() >= 5) {
                return false;
            }
        }
        return player.getStatus() == PlayerStatus.WON
                && players.size() >= 3;
    }
}
