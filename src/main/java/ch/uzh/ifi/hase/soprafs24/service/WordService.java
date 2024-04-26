package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.WordNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WordService {
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
            return wordRepository.saveAndFlush(word);
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

    public Word findRandomWord() {
        Long qty = wordRepository.count();
        int idx = (int) (Math.random() * qty);
        Page<Word> wordPage = wordRepository.findAll(PageRequest.of(idx, 1));
        Word word = null;
        if (wordPage.hasContent()) {
            word = wordPage.getContent().get(0);
        }
        return word;
    }

    public Word getTargetWord(double targetDifficultyScore) {
        return findRandomWord();
    }
}
