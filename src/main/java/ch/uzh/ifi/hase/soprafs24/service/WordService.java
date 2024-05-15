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
