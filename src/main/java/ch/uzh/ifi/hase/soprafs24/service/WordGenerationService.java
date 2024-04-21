package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Service
public class WordGenerationService {

    private final WordService wordService;

    private final CombinationService combinationService;

    @Autowired
    public WordGenerationService(WordService wordService, CombinationService combinationService) {
        this.wordService = wordService;
        this.combinationService = combinationService;
    }

    public void pregenerateDatabase(int numberOfSteps, List<Word> startingWords) throws Exception {
        for (Word word : startingWords)
            wordService.addWord(word);

        for (int step = 1; step <= numberOfSteps; step++) {
            while (true) {
                Word word1 = wordService.findRandomWord();
                Word word2 = wordService.findRandomWord();

                try {
                    combinationService.findCombination(word1, word2);
                }
                catch (Exception e) {
                    int generatedDepth = max(word1.getDepth(), word2.getDepth()) + 1;
                    Combination newCombination = combinationService.getCombination(word1, word2);
                    Word resultWord = newCombination.getResult();
                    resultWord.setDepth(generatedDepth);
                    resultWord.setDifficultyScore((double) 1 / (1L << generatedDepth));

                    try {
                        // The resulting word has been seen before
                        Word foundWord = wordService.findWord(resultWord);
                        foundWord.setDepth(min(foundWord.getDepth(), resultWord.getDepth()));
                        foundWord.setDifficultyScore(foundWord.getDifficultyScore() + resultWord.getDifficultyScore());
                        wordService.updateWord(foundWord);
                    }
                    catch (Exception f) {
                        // The resulting word is seen for the first time
                        wordService.addWord(resultWord);
                    }

                    break;
                }
            }
        }
    }

    public Word getTargetWord(double targetDifficultyScore) {
        return wordService.findRandomWord();
    }
}
