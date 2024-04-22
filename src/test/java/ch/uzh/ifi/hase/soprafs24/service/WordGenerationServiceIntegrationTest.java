package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import ch.uzh.ifi.hase.soprafs24.service.wordgeneration.util.ResultWordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@SpringBootTest
class WordGenerationServiceIntegrationTest {

    @Qualifier("wordRepository")
    @Autowired
    private WordRepository wordRepository;

    @Qualifier("combinationRepository")
    @Autowired
    private CombinationRepository combinationRepository;

    @Autowired
    private WordService wordService;

    @Autowired
    private CombinationService combinationService;

    @Autowired
    private WordGenerationService wordGenerationService;

    @BeforeEach
    public void setup() {
        combinationRepository.deleteAll();
        wordRepository.deleteAll();
        wordGenerationService = new WordGenerationService(wordService,
                combinationService,
                new ResultWordGenerator(wordService, combinationService));
    }

    @Test
    void pregenerateDatabase_tenCombinations_success() throws Exception {
        Word word1 = new Word("Water");
        Word word2 = new Word("Fire");
        ArrayList<Word> startingWords = new ArrayList<>(Arrays.asList(word1, word2));

        wordGenerationService.makeCombinations(10, startingWords);
        Word targetWord = wordGenerationService.getTargetWord(0.25);

        assertEquals(10, combinationRepository.findAll().size());
    }
}
