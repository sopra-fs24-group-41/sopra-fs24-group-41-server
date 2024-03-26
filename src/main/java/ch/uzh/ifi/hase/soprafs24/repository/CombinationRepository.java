package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("combinationRepository")
public interface CombinationRepository extends JpaRepository<Combination, Long> {
    List<Combination> findByResult(Word result);
    List<Combination> findByWord1(Word word1);
    List<Combination> findByWord2(Word word2);
    Combination findByWord1AndWord2(Word word1, Word word2);
}
