package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.service.wordgeneration.util.ResultWordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WordGenerationService {

    private final WordService wordService;

    private final CombinationService combinationService;

    private final ResultWordGenerator resultWordGenerator;

    @Autowired
    public WordGenerationService(WordService wordService, CombinationService combinationService, ResultWordGenerator resultWordGenerator) {
        this.wordService = wordService;
        this.combinationService = combinationService;
        this.resultWordGenerator = resultWordGenerator;
    }

    public void makeCombinations(int numberOfCombinations, List<Word> startingWords) throws Exception {
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
                    combinationService.findCombination(word1, word2);
                }
                catch (CombinationNotFoundException e) {
                    resultWordGenerator.generateResultWord(word1, word2);
                    break;
                }
            }
        }
    }

    public Word getTargetWord(double targetDifficultyScore) {
        return wordService.findRandomWord();
    }
}
