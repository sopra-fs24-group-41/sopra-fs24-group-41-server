package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.WordNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public Word getWordFromString(String name) {
        Word word = wordRepository.findByName(name);
        if (word != null) {
            return word;
        }
        else {
            throw new WordNotFoundException(name);
        }
    }
}
