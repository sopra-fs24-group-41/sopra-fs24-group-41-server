package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.exceptions.WordNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CombinationService {
    private final CombinationRepository combinationRepository;
    @Autowired
    public CombinationService(@Qualifier("combinationRepository") CombinationRepository combinationRepository) {
        this.combinationRepository = combinationRepository;
    }

    public Combination findCombination(Word word1, Word word2) {
        Combination combination = combinationRepository.findByWord1AndWord2(word1, word2);
        if (combination == null) {
            combination = combinationRepository.findByWord1AndWord2(word2, word1);
            if (combination == null) {
                throw new CombinationNotFoundException(word1.getName(), word2.getName());
            }
        }
        return combination;
    }
}
