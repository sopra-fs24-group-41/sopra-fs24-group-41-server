package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.exceptions.WordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class WordGenerationServiceTest {

    @Mock
    private WordService wordService;

    @Mock
    private CombinationService combinationService;

    @InjectMocks
    private WordGenerationService wordGenerationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void generateResultWord_whenResultWordFirstTimeSeen_success() throws Exception {
        Word word1 = new Word("Earthquake", 4, 0.07);
        Word word2 = new Word("Volcano", 3, 0.11);
        Word expectedResultWord = new Word("Apocalypse", 5, (double) 1 / (1L << 5));

        Mockito.when(combinationService.getCombination(any(), any()))
                .thenReturn(new Combination(word1, word2, expectedResultWord));
        Mockito.when(wordService.findWord(any()))
                .thenThrow(new WordNotFoundException(expectedResultWord.getName()));

        Word actualResultWord = wordGenerationService.generateResultWord(word1, word2);

        assertEquals(expectedResultWord.getName(), actualResultWord.getName());
        assertEquals(expectedResultWord.getDepth(), actualResultWord.getDepth());
        assertEquals(expectedResultWord.getDifficultyScore(), actualResultWord.getDifficultyScore());
    }

    @Test
    public void generateResultWord_whenResultWordSeenBefore_updatesDepthAndScore() throws Exception {
        Word word1 = new Word("Earthquake", 4, 0.07);
        Word word2 = new Word("Volcano", 3, 0.11);
        Word oldResultWord = new Word("Apocalypse", 6, (double) 1 / (1L << 6));
        Word updatedResultWord = new Word("Apocalypse", 5, (double) 1 / (1L << 5) + (double) 1 / (1L << 6));

        Mockito.when(combinationService.getCombination(any(), any()))
                .thenReturn(new Combination(word1, word2, new Word("Apocalypse")));
        Mockito.when(wordService.findWord(any()))
                .thenReturn(oldResultWord);
        Mockito.when(wordService.updateWord(any())).thenReturn(updatedResultWord);

        Word actualResultWord = wordGenerationService.generateResultWord(word1, word2);

        assertEquals(updatedResultWord.getName(), actualResultWord.getName());
        assertEquals(updatedResultWord.getDepth(), actualResultWord.getDepth());
        assertEquals(updatedResultWord.getDifficultyScore(), actualResultWord.getDifficultyScore());
    }
}
