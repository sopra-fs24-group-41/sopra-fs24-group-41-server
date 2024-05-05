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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
        makeCombinations(20);
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
        try {
            combination = findCombination(combination.getWord1(), combination.getWord2());
        }
        catch (CombinationNotFoundException ignored) {
        }

        Word word1 = combination.getWord1();
        Word word2 = combination.getWord2();

        int depth = max(word1.getDepth(), word2.getDepth()) + 1;
        double reachability = 1.0 / (1L << depth);

        Word resultWord = combination.getResult();
        resultWord.setDepth(min(resultWord.getDepth(), depth));
        resultWord.setReachability(resultWord.getReachability() + reachability);

        combinationRepository.saveAndFlush(combination);
        wordService.saveWord(resultWord);

        propagateWordUpdates(resultWord);

        return combination;
    }

    void propagateWordUpdates(Word startingWord) {
        Queue<Word> queue = new LinkedList<>();
        queue.add(startingWord);

        Word firstWord;
        Word secondWord;
        Word resultWord;
        List<Combination> adjacencyList;
        while (!queue.isEmpty()) {
            firstWord = queue.remove();
            adjacencyList = new LinkedList<>();
            adjacencyList.addAll(combinationRepository.findByWord1(firstWord));
            adjacencyList.addAll(combinationRepository.findByWord2(firstWord));

            for (Combination combination : adjacencyList) {
                secondWord = (firstWord == combination.getWord1()) ? combination.getWord2() : combination.getWord1();
                resultWord = combination.getResult();
                if (max(firstWord.getDepth(), secondWord.getDepth()) + 1 < resultWord.getDepth()) {
                    saveCombination(combination);
                    queue.add(resultWord);
                }
            }
        }
    }

    public Combination createCustomCombination(Word word1, Word word2, Word result) {
        Combination combination = new Combination(wordService.getWord(word1), wordService.getWord(word2), wordService.getWord(result));
        combination = saveCombination(combination);
        return combination;
    }

    Word generateCombinationResult(Word word1, Word word2) {
        String resultString = apiService.generateCombinationResult(word1.getName(), word2.getName());
        Word word = new Word(resultString);

        int maxIter = 10;
        int iter = 0;
        while (!validResult(word)) {
            resultString = apiService.generateCombinationResult(word1.getName(), word2.getName());
            word = new Word(resultString);

            iter += 1;
            if (iter >= maxIter) {
                throw new RuntimeException("Maximum iteration exceeded, couldn't generate a valid result word!");
            }
        }

        return new Word(resultString);
    }

    public Boolean validResult(Word result) {
        // might add some more validation later, therefore separate method
        return result.getName().trim().length() > 1;
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

    public void makeCombinations(int numberOfCombinations) {
        for (int step = 1; step <= numberOfCombinations; step++) {
            int maxIter = 1000;
            int iter = 0;
            boolean newCombinationFound = false;
            while (!newCombinationFound) {
                Word word1 = wordService.getRandomWord();
                Word word2 = wordService.getRandomWord();

                try {
                    findCombination(word1, word2);
                }
                catch (CombinationNotFoundException e) {
                    createCombination(word1, word2);
                    newCombinationFound = true;
                }

                iter += 1;
                if (iter >= maxIter) {
                    throw new RuntimeException("Maximum iteration exceeded");
                }
            }
        }
    }

    public Word generateWordWithinReachability(double minReachability, double maxReachability) {
        int maxIter = 1000;
        int iter = 0;
        while (true) {
            minReachability *= 0.75;
            maxReachability *= 1.25;

            Word word1 = wordService.getRandomWordWithinReachability(minReachability, maxReachability);
            Word word2 = wordService.getRandomWordWithinReachability(minReachability, maxReachability);

            try {
                findCombination(word1, word2);
            }
            catch (CombinationNotFoundException e) {
                Word result = createCombination(word1, word2).getResult();
                if (minReachability <= result.getReachability() && result.getReachability() <= maxReachability) {
                    return result;
                }
            }

            iter += 1;
            if (iter >= maxIter) {
                throw new RuntimeException("Maximum iteration exceeded");
            }
        }
    }
}
