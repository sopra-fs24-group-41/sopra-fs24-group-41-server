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
    private final List<Word> forbiddenTargetWords = List.of(new Word("zaddy"), new Word("daddy"), new Word("swag"));

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

    public Word selectTargetWord(double minReachability, double maxReachability) {
        return selectTargetWord(minReachability, maxReachability, new ArrayList<>(), 10);
    }

    public Word selectTargetWord(double minReachability, double maxReachability, int maxDepth) {
        return selectTargetWord(minReachability, maxReachability, new ArrayList<>(), maxDepth);
    }

    public Word selectTargetWord(double minReachability, double maxReachability, List<Word> excludedWords) {
        return selectTargetWord(minReachability, maxReachability, new ArrayList<>(), 10);
    }

    public Word selectTargetWord(double minReachability, double maxReachability, List<Word> excludedWords, int maxDepth) {
        List<Word> words = wordRepository.findAllByReachabilityBetween(minReachability, maxReachability);

        words = words.stream()
                .filter(not(excludedWords::contains))
                .filter(not(forbiddenTargetWords::contains))
                .filter(w -> w.getDepth() <= maxDepth)
                .toList();

        if (words.isEmpty()) {
            try {
                return combinationService.generateWordWithinReachability(minReachability, maxReachability);
            } catch (WordNotFoundException e) {
                return getRandomWord();
            }
        }

        return pickRandom(words);
    }

    public Word getRandomWord() {
        return pickRandom(wordRepository.findAll());
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
