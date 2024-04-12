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
    private final WordService wordService;

    @Autowired
    public CombinationService(@Qualifier("combinationRepository") CombinationRepository combinationRepository, APIService apiService, WordService wordService) {
        this.combinationRepository = combinationRepository;
        this.apiService = apiService;
        this.wordService = wordService;
    }

    public Combination getCombination(Word word1, Word word2) {
        try {
            return findCombination(word1, word2);
        }
        catch (CombinationNotFoundException e) {
            return createCombination(word1, word2);
        }
    }

    public Combination findCombination(Word word1, Word word2) {
        Combination combination = combinationRepository.findByWord1AndWord2(word1, word2);
        if (combination != null) return combination;

        combination = combinationRepository.findByWord1AndWord2(word2, word1);
        if (combination != null) return combination;

        throw new CombinationNotFoundException(word1.getName(), word2.getName());
    }

    private Combination createCombination(Word word1, Word word2) {
        Word resultWord = generateCombinationResult(word1, word2);
        Combination newCombination = new Combination(wordService.getWord(word1), wordService.getWord(word2), wordService.getWord(resultWord));
        return combinationRepository.saveAndFlush(newCombination);
    }

    private Word generateCombinationResult(Word word1, Word word2) {
        String resultString = apiService.generateCombinationResult(word1.getName(), word2.getName());
        return new Word(resultString);
    }

}
