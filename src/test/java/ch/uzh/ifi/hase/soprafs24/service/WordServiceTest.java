package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class WordServiceTest {
    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordService wordService;

    private Word testWord;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testWord = new Word("Water");

        Mockito.when(wordRepository.saveAndFlush(Mockito.any())).thenReturn(testWord);
        Mockito.when(wordRepository.save(Mockito.any())).thenReturn(testWord);
    }

    @Test
    void getWord_foundWord() {
        Mockito.when(wordRepository.findByName(Mockito.any())).thenReturn(testWord);

        Word foundWord = wordService.getWord(testWord);

        assertEquals(testWord.getName(), foundWord.getName());
    }

    @Test
    void getWord_newWord() {
        Mockito.when(wordRepository.findByName(Mockito.any())).thenReturn(null);

        Word foundWord = wordService.getWord(testWord);

        assertEquals(testWord.getName(), foundWord.getName());
    }

    @Test
    void depthFromReachability_correct() {
        for (int expectedDepth = 0; expectedDepth <= 10; expectedDepth++) {
            double reachability = 1.0 / (1L << expectedDepth);
            int actualDepth = wordService.depthFromReachability(reachability);
            assertEquals(expectedDepth, actualDepth);
        }
    }
}