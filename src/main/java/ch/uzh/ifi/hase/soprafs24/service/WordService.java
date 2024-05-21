package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import static java.util.function.Predicate.not;

@Service
@Transactional
public class WordService {
    private final Logger log = LoggerFactory.getLogger(WordService.class);
    private final WordRepository wordRepository;

    @Autowired
    public WordService(@Qualifier("wordRepository") WordRepository wordRepository) {
        this.wordRepository = wordRepository;
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

        int startIndex = (int) Math.floor(lowerPercentage * words.size()) - 1;
        int endIndex = (int) Math.ceil(upperPercentage * words.size());

        double maxReachability = words.get(startIndex).getReachability();
        double minReachability = words.get(endIndex).getReachability();

        words = words.stream()
                .filter(w -> w.getReachability() <= maxReachability)
                .filter(w -> w.getReachability() >= minReachability)
                .filter(not(excludedWords::contains))
                .toList();

        if (words.isEmpty()) throw new RuntimeException("Could not find any words with desired reachability!");

        return pickRandom(words.subList(startIndex, endIndex));
    }

    public Word getRandomWord() {
        return pickRandom(wordRepository.findAll());
    }

    public Word getRandomWordWithinReachability(double minReachability, double maxReachability) {
        return pickRandom(wordRepository.findAllByReachabilityBetween(minReachability, maxReachability));
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
}
