package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findById(long id);

    Player findByToken(String token);

    Player findByUser(User user);

    Player findByOwnedLobby(Lobby ownedLobby);

    List<Player> findAllByLobby(Lobby lobby);
}
