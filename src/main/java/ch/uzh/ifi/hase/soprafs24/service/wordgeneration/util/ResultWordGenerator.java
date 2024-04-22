package ch.uzh.ifi.hase.soprafs24.service.wordgeneration.util;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Component
public class ResultWordGenerator {

    private final WordService wordService;

    private final CombinationService combinationService;

    @Autowired
    public ResultWordGenerator(WordService wordService, CombinationService combinationService) {
        this.wordService = wordService;
        this.combinationService = combinationService;
    }

    public Word generateResultWord(Word word1, Word word2) {
        int generatedDepth = max(word1.getDepth(), word2.getDepth()) + 1;
        Combination newCombination = combinationService.getCombination(word1, word2);
        Word resultWord = newCombination.getResult();
        resultWord.setDepth(generatedDepth);
        resultWord.setReachability((double) 1 / (1L << generatedDepth));

        Word foundWord = wordService.getWord(resultWord);
        foundWord.setDepth(min(foundWord.getDepth(), resultWord.getDepth()));
        foundWord.setReachability(foundWord.getReachability() + resultWord.getReachability());
        return foundWord;
    }
}
