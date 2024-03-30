package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.PlayerWord;
import ch.uzh.ifi.hase.soprafs24.entity.PlayerWordId;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerWordRepository")
public interface PlayerWordRepository extends JpaRepository<PlayerWord, PlayerWordId> {
    List<PlayerWord> findAllByPlayer(Player player);

    List<PlayerWord> findAllByWord(Word word);
}
