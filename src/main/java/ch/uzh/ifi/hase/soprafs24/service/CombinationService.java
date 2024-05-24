package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.exceptions.WordNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Math.max;

@Service
@Transactional(noRollbackFor = WordNotFoundException.class)
public class CombinationService {
    private final CombinationRepository combinationRepository;
    private final APIService apiService;
    private final WordService wordService;
    private final List<Word> deadEndWords = List.of(new Word("zaddy"), new Word("daddy"), new Word("swag"));

    @Autowired
    public CombinationService(@Qualifier("combinationRepository") CombinationRepository combinationRepository, APIService apiService, WordService wordService) {
        this.combinationRepository = combinationRepository;
        this.apiService = apiService;
        this.wordService = wordService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setupCombinationDatabase() {
        makeDefaultCombinations();
        makeZaddyChain();
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
        Word combinationResult = deadEndWords.contains(word1) ? word1 : deadEndWords.contains(word2) ? word2 : generateCombinationResult(word1, word2);

        Combination combination = new Combination(wordService.getWord(word1), wordService.getWord(word2), wordService.getWord(combinationResult));
        combination = saveCombination(combination);
        return combination;
    }

    public Combination saveCombination(Combination combination) {
        boolean isNewCombination;
        try {
            combination = findCombination(combination.getWord1(), combination.getWord2());
            isNewCombination = false;
        }
        catch (CombinationNotFoundException ignored) {
            isNewCombination = true;
        }

        Word resultWord = combination.getResult();
        resultWord.updateDepth(combination.getWord1().getDepth(), combination.getWord2().getDepth());
        if (!isNewCombination && resultWord.getDepth() != 0) {
            // Subtract the reachability previously added by the combination with the old depth
            double oldReachability = 1.0 / (1L << combination.getDepth());
            resultWord.setReachability(resultWord.getReachability() - oldReachability);
        }
        resultWord.updateReachability();

        combination.setDepth(resultWord.getDepth());

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

        return word;
    }

    public Boolean validResult(Word result) {
        // might add some more validation later, therefore separate method
        return result.getName().trim().length() > 1 && result.getName().trim().length() <= 20;
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
        assert (minReachability < maxReachability);

        int maxDepth = wordService.depthFromReachability(minReachability) + 1;  // since it's floor when casting to int
        int minDepth = wordService.depthFromReachability(maxReachability);

        for (int i = 0; i <= 100; i += 1) {
            Word word1 = wordService.getRandomWordWithinDepth(minDepth - 1, maxDepth - 1);
            if (word1 == null) {
                word1 = wordService.getRandomWord();
            }
            Word word2 = wordService.getRandomWordWithinDepth(minDepth - 1, maxDepth - 1);
            if (word2 == null) {
                word2 = wordService.getRandomWord();
            }
            try {
                findCombination(word1, word2);
            }
            catch (CombinationNotFoundException e) {
                Word result = createCombination(word1, word2).getResult();
                if (result.getReachability() != null && result.getReachability() >= minReachability && result.getReachability() <= maxReachability) {
                    return result;
                }
            }
        }
        throw new WordNotFoundException("within reachability");
    }

    private void makeDefaultCombinations() {
        wordService.saveWord(new Word("water", 0, null));
        wordService.saveWord(new Word("earth", 0, null));
        wordService.saveWord(new Word("fire", 0, null));
        wordService.saveWord(new Word("air", 0, null));

        createCustomCombination(new Word("water"), new Word("water"), new Word("lake"));
        createCustomCombination(new Word("water"), new Word("earth"), new Word("mud"));
        createCustomCombination(new Word("water"), new Word("fire"), new Word("steam"));
        createCustomCombination(new Word("water"), new Word("air"), new Word("mist"));
        createCustomCombination(new Word("earth"), new Word("earth"), new Word("hill"));
        createCustomCombination(new Word("earth"), new Word("fire"), new Word("lava"));
        createCustomCombination(new Word("earth"), new Word("air"), new Word("dust"));
        createCustomCombination(new Word("fire"), new Word("fire"), new Word("wildfire"));
        createCustomCombination(new Word("fire"), new Word("air"), new Word("smoke"));
        createCustomCombination(new Word("air"), new Word("air"), new Word("wind"));
    }

    private void makeZaddyChain() {
        createCustomCombination(new Word("fire"), new Word("dust"), new Word("ash"));
        createCustomCombination(new Word("lake"), new Word("lake"), new Word("ocean"));
        createCustomCombination(new Word("hill"), new Word("hill"), new Word("mountain"));
        createCustomCombination(new Word("lava"), new Word("lava"), new Word("magma"));

        createCustomCombination(new Word("fire"), new Word("ash"), new Word("charcoal"));
        createCustomCombination(new Word("earth"), new Word("magma"), new Word("rock"));
        createCustomCombination(new Word("ocean"), new Word("mountain"), new Word("island"));

        createCustomCombination(new Word("fire"), new Word("rock"), new Word("metal"));
        createCustomCombination(new Word("charcoal"), new Word("charcoal"), new Word("coal"));
        createCustomCombination(new Word("island"), new Word("island"), new Word("continent"));

        createCustomCombination(new Word("ocean"), new Word("continent"), new Word("planet"));
        createCustomCombination(new Word("coal"), new Word("coal"), new Word("carbon"));

        createCustomCombination(new Word("fire"), new Word("planet"), new Word("sun"));
        createCustomCombination(new Word("carbon"), new Word("carbon"), new Word("diamond"));

        createCustomCombination(new Word("water"), new Word("sun"), new Word("life"));
        createCustomCombination(new Word("metal"), new Word("sun"), new Word("gold"));

        createCustomCombination(new Word("air"), new Word("life"), new Word("animal"));
        createCustomCombination(new Word("diamond"), new Word("gold"), new Word("swag"));

        createCustomCombination(new Word("animal"), new Word("animal"), new Word("human"));

        createCustomCombination(new Word("earth"), new Word("human"), new Word("man"));
        createCustomCombination(new Word("human"), new Word("human"), new Word("family"));

        createCustomCombination(new Word("man"), new Word("family"), new Word("father"));

        createCustomCombination(new Word("fire"), new Word("father"), new Word("daddy"));

        createCustomCombination(new Word("swag"), new Word("daddy"), new Word("zaddy"));
    }
}
