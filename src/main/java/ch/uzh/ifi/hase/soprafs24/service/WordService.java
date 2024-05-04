package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.WordNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        try {
            return findWord(word);
        }
        catch (WordNotFoundException e) {
            try {
                return wordRepository.saveAndFlush(word);
            }
            catch (Exception ex) {
                log.error("Error saving word: {}", word, ex);
                throw ex;
            }
        }
        catch (Exception e) {
            log.error("Unexpected error in getWord for word: {}", word, e);
            throw e;
        }
    }

    public Word saveWord(Word word) {
        return wordRepository.saveAndFlush(word);
    }

    public Word findWord(Word word) {
        Word foundWord = wordRepository.findByName(word.getName());
        if (foundWord != null) return foundWord;

        throw new WordNotFoundException(word.getName());
    }

    public Word getRandomWord() {
        Long qty = wordRepository.count();
        int idx = (int) (Math.random() * qty);
        Page<Word> wordPage = wordRepository.findAll(PageRequest.of(idx, 1));
        Word word = null;
        if (wordPage.hasContent()) {
            word = wordPage.getContent().get(0);
        }
        return word;
    }

    public Word getRandomWordWithinReachability(double minReachability, double maxReachability) {
        List<Word> wordList = wordRepository.findAllByReachabilityBetween(minReachability, maxReachability);
        int count = wordList.size();
        if (count == 0) {
            throw new WordNotFoundException("Couldn't find a word within the reachability");
        }

        int idx = (int) (Math.random() * count);

        return wordList.get(idx);
    }
}
