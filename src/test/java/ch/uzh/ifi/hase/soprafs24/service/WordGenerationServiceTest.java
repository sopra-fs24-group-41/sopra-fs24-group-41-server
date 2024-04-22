package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.service.wordgeneration.util.ResultWordGenerator;
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

class WordGenerationServiceTest {

    @Mock
    private WordService wordService;

    @Mock
    private CombinationService combinationService;

    @Mock
    private ResultWordGenerator resultWordGenerator;

    @InjectMocks
    private WordGenerationService wordGenerationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void pregenerateDatabase_oneCombination_success() throws Exception {
        Word word1 = new Word("Water");
        Word word2 = new Word("Fire");
        ArrayList<Word> startingWords = new ArrayList<>(Arrays.asList(word1, word2));
        Word expectedResultWord = new Word("Steam", 1, (double) 1 / (1L << 1));
        ArrayList<Word> expectedResultList = new ArrayList<Word>(Arrays.asList(word1, word2, expectedResultWord));
        ArrayList<Word> actualResultList = new ArrayList<>();

        Mockito.when(wordService.getWord(word1)).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                actualResultList.add(word1);
                return word1;
            }
        });
        Mockito.when(wordService.getWord(word2)).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                actualResultList.add(word2);
                return word2;
            }
        });

        Mockito.when(wordService.findRandomWord()).thenReturn(word1, word2);
        Mockito.when(combinationService.findCombination(any(), any()))
                .thenThrow(new CombinationNotFoundException(word1.getName(), word2.getName()));

        Mockito.when(resultWordGenerator.generateResultWord(any(), any()))
                .thenAnswer(new Answer() {
                    public Object answer(InvocationOnMock invocation) {
                        actualResultList.add(expectedResultWord);
                        return expectedResultWord;
                    }
                });

        wordGenerationService.makeCombinations(1, startingWords);

        assertEquals(expectedResultList, actualResultList);
    }
}
