package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findById(long id);

    Player findByToken(String token);

    Player findByUser_Id(Long user_id);

    Player findByOwnedLobby_Code(long ownedLobby_code);

    List<Player> findAllByLobby_Code(long lobby_code);
}
