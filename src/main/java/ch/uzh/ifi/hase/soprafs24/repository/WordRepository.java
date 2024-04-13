package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("wordRepository")
public interface WordRepository extends JpaRepository<Word, Long> {
    Word findByName(String name);
    Page<Word> findAll(Pageable pageable);
}
