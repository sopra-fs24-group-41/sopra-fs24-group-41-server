package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("wordRepository")
public interface WordRepository extends JpaRepository<Word, Long> {
    Word findByName(String name);

    List<Word> findAllByReachabilityBetween(double start, double end);

    List<Word> findAllByDepthBetween(int start, int end);

    @Query(value = "SELECT word FROM Word word WHERE word.reachability IS NOT NULL ORDER BY word.reachability DESC")
    List<Word> findAllSortedByDescendingReachability();
}
