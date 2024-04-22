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
    public Word generateResultWord(Word word1, Word word2) throws Exception {
        int generatedDepth = max(word1.getDepth(), word2.getDepth()) + 1;
        Combination newCombination = combinationService.getCombination(word1, word2);
        Word resultWord = newCombination.getResult();
        resultWord.setDepth(generatedDepth);
        resultWord.setDifficultyScore((double) 1 / (1L << generatedDepth));

        try {
            // The resulting word has been seen before
            Word foundWord = wordService.findWord(resultWord);
            resultWord.setDepth(min(foundWord.getDepth(), resultWord.getDepth()));
            resultWord.setDifficultyScore(foundWord.getDifficultyScore() + resultWord.getDifficultyScore());
            wordService.updateWord(resultWord);
        }
        catch (Exception f) {
            // The resulting word is seen for the first time
            wordService.addWord(resultWord);
        }
        return resultWord;
    }
}
