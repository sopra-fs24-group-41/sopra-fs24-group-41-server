package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CombinationService {
    private final CombinationRepository combinationRepository;
    private final APIService apiService;

    @Autowired
    public CombinationService(@Qualifier("combinationRepository") CombinationRepository combinationRepository, APIService apiService) {
        this.combinationRepository = combinationRepository;
        this.apiService = apiService;
    }

    public Combination getCombination(Word word1, Word word2) {
        Combination combination;
        try {
            combination = findCombination(word1, word2);
        }
        catch (CombinationNotFoundException e) {
            combination = createCombination(word1, word2);
        }
        return combination;
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

    private Combination createCombination(Word word1, Word word2) {
        String resultString = apiService.generateCombinationResult(word1.getName(), word2.getName());
        Word result = new Word(resultString);
        Combination newCombination = new Combination(word1, word2, result);
        newCombination = combinationRepository.save(newCombination);
        combinationRepository.flush();
        return newCombination;
    }
}
