package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.WordNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import static java.util.function.Predicate.not;

@Service
@Transactional(noRollbackFor = WordNotFoundException.class)
public class WordService {
    private final WordRepository wordRepository;
    private final CombinationService combinationService;

    @Autowired
    public WordService(@Qualifier("wordRepository") WordRepository wordRepository, @Lazy CombinationService combinationService) {
        this.wordRepository = wordRepository;
        this.combinationService = combinationService;
    }

    public Word getWord(Word word) {
        Word foundWord = findWord(word);
        if (foundWord == null) {
            Word savedWord = wordRepository.saveAndFlush(word);
            savedWord.setNewlyDiscovered(true);
            return savedWord;
        }
        return foundWord;
    }

    public Word saveWord(Word word) {
        return wordRepository.saveAndFlush(word);
    }

    public Word findWord(Word word) {
        return wordRepository.findByName(word.getName());
    }

    public Word selectTargetWord(float desiredDifficulty) {
        return selectTargetWord(desiredDifficulty, new ArrayList<>());
    }

    public Word selectTargetWord(float desiredDifficulty, List<Word> excludedWords) {
        List<Word> words = wordRepository.findAllSortedByDescendingReachability();

        float margin = 0.05f;

        float lowerPercentage = clamp(0, desiredDifficulty - margin, 1);
        float upperPercentage = clamp(0, desiredDifficulty + margin, 1);

        int startIndex = (int) Math.floor(lowerPercentage * words.size());
        int endIndex = (int) Math.ceil(upperPercentage * words.size()) - 1;

        double maxReachability = words.get(startIndex).getReachability();
        double minReachability = words.get(endIndex).getReachability();

        words = words.stream()
                .filter(w -> w.getReachability() <= maxReachability)
                .filter(w -> w.getReachability() >= minReachability)
                .filter(not(excludedWords::contains))
                .toList();

        if (words.isEmpty()) {
            try {
                return combinationService.generateWordWithinReachability(minReachability, maxReachability);
            } catch (WordNotFoundException e) {
                return null;
            }
        }

        return pickRandom(words);
    }

    public Word getRandomWord() {
        return pickRandom(wordRepository.findAll());
    }

    public Word getRandomWordWithinReachability(double minReachability, double maxReachability) {
        return pickRandom(wordRepository.findAllByReachabilityBetween(minReachability, maxReachability));
    }

    public Word getRandomWordWithinDepth(int minDepth, int maxDepth) {
        return pickRandom(wordRepository.findAllByDepthBetween(minDepth, maxDepth));
    }

    private <T> T pickRandom(List<T> objects) {
        int count = objects.size();
        if (count == 0) return null;
        int idx = (int) (Math.random() * count);
        return objects.get(idx);
    }

    private float clamp(float lower, float value, float upper) {
        return Math.max(lower, Math.min(value, upper));
    }

    public int depthFromReachability(double reachability) {
        return (int) (Math.log(1.0 / reachability) / Math.log(2));
    }
}
