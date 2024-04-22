package ch.uzh.ifi.hase.soprafs24.service.wordgeneration.util;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.WordNotFoundException;
import ch.uzh.ifi.hase.soprafs24.service.CombinationService;
import ch.uzh.ifi.hase.soprafs24.service.WordGenerationService;
import ch.uzh.ifi.hase.soprafs24.service.WordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class ResultWordGeneratorTest {

    @Mock
    private WordService wordService;

    @Mock
    private CombinationService combinationService;

    @InjectMocks
    private ResultWordGenerator resultWordGenerator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void generateResultWord_whenResultWordFirstTimeSeen_success() throws Exception {
        Word word1 = new Word("Earthquake", 4, 0.07);
        Word word2 = new Word("Volcano", 3, 0.11);
        Word expectedResultWord = new Word("Apocalypse", 5, (double) 1 / (1L << 5));

        Mockito.when(combinationService.getCombination(any(), any()))
                .thenReturn(new Combination(word1, word2, expectedResultWord));
        Mockito.when(wordService.findWord(any()))
                .thenThrow(new WordNotFoundException(expectedResultWord.getName()));

        Word actualResultWord = resultWordGenerator.generateResultWord(word1, word2);

        assertEquals(expectedResultWord.getName(), actualResultWord.getName());
        assertEquals(expectedResultWord.getDepth(), actualResultWord.getDepth());
        assertEquals(expectedResultWord.getDifficultyScore(), actualResultWord.getDifficultyScore());
    }

    @Test
    void generateResultWord_whenResultWordSeenBefore_updatesDepthAndScore() throws Exception {
        Word word1 = new Word("Earthquake", 4, 0.07);
        Word word2 = new Word("Volcano", 3, 0.11);
        Word oldResultWord = new Word("Apocalypse", 6, (double) 1 / (1L << 6));
        Word updatedResultWord = new Word("Apocalypse", 5, (double) 1 / (1L << 5) + (double) 1 / (1L << 6));

        Mockito.when(combinationService.getCombination(any(), any()))
                .thenReturn(new Combination(word1, word2, new Word("Apocalypse")));
        Mockito.when(wordService.findWord(any()))
                .thenReturn(oldResultWord);
        Mockito.when(wordService.updateWord(any())).thenReturn(updatedResultWord);

        Word actualResultWord = resultWordGenerator.generateResultWord(word1, word2);

        assertEquals(updatedResultWord.getName(), actualResultWord.getName());
        assertEquals(updatedResultWord.getDepth(), actualResultWord.getDepth());
        assertEquals(updatedResultWord.getDifficultyScore(), actualResultWord.getDifficultyScore());
    }
}
