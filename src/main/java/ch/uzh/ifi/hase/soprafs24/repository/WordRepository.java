package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("wordRepository")
public interface WordRepository extends JpaRepository<Word, Long> {
    Word findByName(String name);


    @Query(
            "SELECT word FROM Word word " +
            "WHERE LOWER(REPLACE(CASE " +
            "WHEN word.name LIKE '%es' THEN SUBSTRING(word.name, 1, LENGTH(word.name) - 2) " +
            "WHEN word.name LIKE '%s' THEN SUBSTRING(word.name, 1, LENGTH(word.name) - 1) " +
            "ELSE word.name END, ' ', '')) " +
            "LIKE LOWER(:name)"
    )
    Word findBySimilarName(String name);

    List<Word> findAllByReachabilityBetween(double start, double end);

    List<Word> findAllByDepthBetween(int start, int end);

    @Query(value = "SELECT word FROM Word word WHERE word.reachability IS NOT NULL ORDER BY word.reachability DESC")
    List<Word> findAllSortedByDescendingReachability();
}
