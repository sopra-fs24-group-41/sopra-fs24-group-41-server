package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("lobbyRepository")
public interface LobbyRepository extends JpaRepository<Lobby, Long> {
    Lobby findByCode(long code);

    List<Lobby> findAllByPublicAccess(boolean publicAccess);

    Lobby findByOwner(Player player);

    List<Lobby> findAllByMode(GameMode mode);

    List<Lobby> findAllByStatus(LobbyStatus status);

    Lobby findByPlayersIsContaining(Player player);
}
