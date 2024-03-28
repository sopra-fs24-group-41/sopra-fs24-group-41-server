package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.PlayerWord;
import ch.uzh.ifi.hase.soprafs24.entity.PlayerWordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("playerWordRepository")
public interface PlayerWordRepository extends JpaRepository<PlayerWord, PlayerWordId> {
}
