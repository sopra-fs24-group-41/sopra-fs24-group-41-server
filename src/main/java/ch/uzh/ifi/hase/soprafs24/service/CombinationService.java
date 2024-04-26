package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @EventListener(ApplicationReadyEvent.class)
    public void setupCombinationDatabase() {
        List<Word> startingWords = new ArrayList<Word>();
        startingWords.add(new Word("water"));
        startingWords.add(new Word("earth"));
        startingWords.add(new Word("fire"));
        startingWords.add(new Word("air"));

        makeDefaultCombinations(startingWords);
        makeCombinations(20, startingWords);
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

    public Combination createCombination(Word word1, Word word2) {
        Word combinationResult = generateCombinationResult(word1, word2);
        Combination combination = new Combination(wordService.getWord(word1), wordService.getWord(word2), wordService.getWord(combinationResult));
        combination = saveCombination(combination);
        return combination;
    }

    public Combination saveCombination(Combination combination) {
        Word word1 = combination.getWord1();
        Word word2 = combination.getWord2();

        int depth = max(word1.getDepth(), word2.getDepth()) + 1;
        double reachability = 1.0 / (1L << depth);

        combination = combinationRepository.saveAndFlush(combination);

        Word resultWord = combination.getResult();
        resultWord.setDepth(min(resultWord.getDepth(), depth));
        resultWord.setReachability(resultWord.getReachability() + reachability);

        wordService.saveWord(resultWord);

        return combination;
    }

    public Combination createCustomCombination(Word word1, Word word2, Word result) {
        Combination combination = new Combination(wordService.getWord(word1), wordService.getWord(word2), wordService.getWord(result));
        combination = saveCombination(combination);
        return combination;
    }

    Word generateCombinationResult(Word word1, Word word2) {
        String resultString = apiService.generateCombinationResult(word1.getName(), word2.getName());
        return new Word(resultString);
    }

    public void makeDefaultCombinations(List<Word> startingWords) {
        for (Word word : startingWords) {
            Word foundWord = wordService.getWord(word);
            foundWord.setDepth(0);
            foundWord.setReachability(1e6);
            wordService.saveWord(foundWord);
        }

        createCustomCombination(new Word("water"), new Word("water"), new Word("water"));
        createCustomCombination(new Word("water"), new Word("earth"), new Word("mud"));
        createCustomCombination(new Word("water"), new Word("fire"), new Word("steam"));
        createCustomCombination(new Word("water"), new Word("air"), new Word("mist"));
        createCustomCombination(new Word("earth"), new Word("earth"), new Word("earth"));
        createCustomCombination(new Word("earth"), new Word("fire"), new Word("lava"));
        createCustomCombination(new Word("earth"), new Word("air"), new Word("dust"));
        createCustomCombination(new Word("fire"), new Word("fire"), new Word("fire"));
        createCustomCombination(new Word("fire"), new Word("air"), new Word("smoke"));
        createCustomCombination(new Word("air"), new Word("air"), new Word("air"));
    }

    public void makeCombinations(int numberOfCombinations, List<Word> startingWords) {
        for (Word word : startingWords) {
            Word foundWord = wordService.getWord(word);
            foundWord.setDepth(0);
            foundWord.setReachability(1e6);
        }

        for (int step = 1; step <= numberOfCombinations; step++) {
            int maxIter = 1000;
            int iter = 0;
            while (true) {
                Word word1 = wordService.getRandomWord();
                Word word2 = wordService.getRandomWord();

                try {
                    findCombination(word1, word2);
                }
                catch (CombinationNotFoundException e) {
                    createCombination(word1, word2);
                    break;
                }

                iter += 1;
                if (iter >= maxIter) {
                    throw new RuntimeException("Maximum iteration exceeded");
                }
            }
        }
    }
}
