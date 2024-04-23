package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

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

    Combination createCombination(Word word1, Word word2) {
        int depth = max(word1.getDepth(), word2.getDepth()) + 1;
        double reachability = 1.0 / (1L << depth);

        Word combinationResult = generateCombinationResult(word1, word2);
        Combination newCombination = new Combination(wordService.getWord(word1), wordService.getWord(word2), wordService.getWord(combinationResult));
        Combination combination = combinationRepository.saveAndFlush(newCombination);

        Word resultWord = combination.getResult();
        resultWord.setDepth(min(resultWord.getDepth(), depth));
        resultWord.setReachability(resultWord.getReachability() + reachability);
        return combination;
    }

    Word generateCombinationResult(Word word1, Word word2) {
        String resultString = apiService.generateCombinationResult(word1.getName(), word2.getName());
        return new Word(resultString);
    }

    public void makeCombinations(int numberOfCombinations, List<Word> startingWords) {
        for (Word word : startingWords) {
            Word foundWord = wordService.getWord(word);
            foundWord.setDepth(0);
            foundWord.setReachability(1e6);
        }

        for (int step = 1; step <= numberOfCombinations; step++) {
            while (true) {
                Word word1 = wordService.findRandomWord();
                Word word2 = wordService.findRandomWord();

                try {
                    findCombination(word1, word2);
                }
                catch (CombinationNotFoundException e) {
                    Combination newCombination = createCombination(word1, word2);
                    Word foundWord = wordService.getWord(newCombination.getResult());
                    foundWord.setDepth(foundWord.getDepth());
                    foundWord.setReachability(foundWord.getReachability());
                    break;
                }
            }
        }
    }
}
