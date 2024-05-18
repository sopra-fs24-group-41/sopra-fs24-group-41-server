package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CombinationServiceTest {

    private Word word1;
    private Word word2;
    private Word result1;
    private Combination combination1;

    private Word word3;
    private Word word4;
    private Word result2;
    private Combination combination2;

    @Mock
    private CombinationRepository combinationRepository;

    @Mock
    private APIService apiService;

    @Mock
    private WordService wordService;

    @Spy
    @InjectMocks
    private CombinationService combinationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        word1 = new Word("water");
        word2 = new Word("earth");
        result1 = new Word("mud");
        combination1 = new Combination(word1, word2, result1);

        word3 = new Word("earthquake", 4, 0.07);
        word4 = new Word("volcano", 3, 0.11);
        result2 = new Word("apocalypse", 5, (double) 1 / (1L << 5));
        combination2 = new Combination(word3, word4, result2);

        Mockito.when(wordService.saveWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(combinationRepository.saveAndFlush(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(combinationRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(combinationRepository.findByWord1AndWord2(word1, word2)).thenReturn(combination1);
        Mockito.when(apiService.generateCombinationResult(word1.getName(), word2.getName())).thenReturn(result1.getName());
    }

    @Test
    void findCombination_success() {
        Combination foundCombination = combinationService.findCombination(word1, word2);
        assertEquals(combination1, foundCombination);
    }

    @Test
    void findSwappedCombination_success() {
        Combination foundCombination = combinationService.findCombination(word2, word1);  // swapped words
        assertEquals(combination1, foundCombination);
    }

    @Test
    void findCombination_throwsException() {
        assertThrows(CombinationNotFoundException.class, () -> combinationService.findCombination(word1, word3));
    }

    @Test
    void getCombination_existingCombination_success() {
        Combination foundCombination = combinationService.getCombination(word1, word2);
        assertEquals(combination1, foundCombination);
    }

    @Test
    void getCombination_newCombination_success() {
        Combination newCombination = combinationService.getCombination(word1, word2);
        assertEquals(combination1, newCombination);
    }

    @Test
    void createCombination_whenResultWordFirstTimeSeen_success() {
        Mockito.when(apiService.generateCombinationResult(word3.getName(), word4.getName())).thenReturn(result2.getName());
        Combination actualCombination = combinationService.createCombination(word3, word4);
        assertEquals(combination2, actualCombination);
    }

    @Test
    void generateResultWord_whenResultWordSeenBefore_updatesDepthAndScore() {
        Word updatedResultWord = new Word("apocalypse", 5, (double) 1 / (1L << 5) + (double) 1 / (1L << 6));
        Combination expectedCombination = new Combination(word3, word4, updatedResultWord);
        Mockito.when(apiService.generateCombinationResult(word3.getName(), word4.getName())).thenReturn(result2.getName());
        Combination actualCombination = combinationService.createCombination(word3, word4);
        assertEquals(expectedCombination, actualCombination);
    }

    @Test
    void makeCombinations_oneCombination_success() {
        ArrayList<Word> expectedResultList = new ArrayList<>(Arrays.asList(word1, word2, result1));
        ArrayList<Word> actualResultList = new ArrayList<>(Arrays.asList(word1, word2));

        Mockito.when(wordService.getRandomWord()).thenReturn(word1, word2);

        Mockito.doThrow(new CombinationNotFoundException(word1.getName(), word2.getName())).when(combinationService).findCombination(word1, word2);

        Mockito.doAnswer(new Answer() {
                    public Object answer(InvocationOnMock invocation) {
                        actualResultList.add(result1);
                        return new Combination(word1, word2, result1);
                    }
                })
                .when(combinationService).createCombination(word1, word2);

        combinationService.makeCombinations(1);

        assertEquals(expectedResultList, actualResultList);
    }

    @Test
    void generateCombinationResult_invalidResult_throwsException() {
        Mockito.when(apiService.generateCombinationResult(Mockito.any(), Mockito.any())).thenReturn("          ");
        assertThrows(RuntimeException.class, () -> combinationService.generateCombinationResult(word1, word2));
    }

    @Test
    void generateCombinationResult_validResultAfterIterations_success() {
        // Return the result after three tries
        Mockito.when(apiService.generateCombinationResult(Mockito.any(), Mockito.any())).thenReturn(""," ", result1.getName());
        assertEquals(result1, combinationService.generateCombinationResult(word1, word2));
    }
}
