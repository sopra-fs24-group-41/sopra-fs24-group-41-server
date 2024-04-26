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
import static org.mockito.ArgumentMatchers.any;

public class CombinationServiceTest {

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
    }

    @Test
    public void findCombination_success() {
        Word testWord1 = new Word("Water");
        Word testWord2 = new Word("Fire");
        Word testResult = new Word("Steam");

        Combination testCombination = new Combination(testWord1, testWord2, testResult);

        Mockito.when(combinationRepository.save(Mockito.any())).thenReturn(testCombination);
        Mockito.when(combinationRepository.findByWord1AndWord2(testWord1, testWord2)).thenReturn(testCombination);

        Combination foundCombination = combinationService.findCombination(testWord1, testWord2);

        assertEquals(testCombination.getId(), foundCombination.getId());
        assertEquals(testCombination.getWord1(), foundCombination.getWord1());
        assertEquals(testCombination.getWord2(), foundCombination.getWord2());
        assertEquals(testCombination.getResult(), foundCombination.getResult());
    }

    @Test
    public void findSwappedCombination_success() {
        Word testWord1 = new Word("Water");
        Word testWord2 = new Word("Fire");
        Word testResult = new Word("Steam");

        Combination testCombination = new Combination(testWord1, testWord2, testResult);

        Mockito.when(combinationRepository.save(Mockito.any())).thenReturn(testCombination);
        Mockito.when(combinationRepository.findByWord1AndWord2(testWord1, testWord2)).thenReturn(testCombination);

        Combination foundCombination = combinationService.findCombination(testWord2, testWord1);  // swapped words

        assertEquals(testCombination.getId(), foundCombination.getId());
        assertEquals(testCombination.getWord1(), foundCombination.getWord1());
        assertEquals(testCombination.getWord2(), foundCombination.getWord2());
        assertEquals(testCombination.getResult(), foundCombination.getResult());
    }

    @Test
    public void findCombination_throwsException() {
        Word testWord1 = new Word("Water");
        Word testWord2 = new Word("Fire");

        Mockito.when(combinationRepository.findByWord1AndWord2(testWord1, testWord2)).thenReturn(null);

        assertThrows(CombinationNotFoundException.class, () -> combinationService.findCombination(testWord1, testWord2));
    }

    @Test
    public void getCombination_existingCombination_success() {
        Word testWord1 = new Word("Water");
        Word testWord2 = new Word("Fire");
        Word testResult = new Word("Steam");

        Combination testCombination = new Combination(testWord1, testWord2, testResult);

        Mockito.when(combinationRepository.findByWord1AndWord2(testWord1, testWord2)).thenReturn(testCombination);
        Mockito.when(combinationRepository.findByWord1AndWord2(testWord2, testWord1)).thenReturn(testCombination);

        Combination foundCombination = combinationService.getCombination(testWord1, testWord2);

        assertEquals(testCombination.getId(), foundCombination.getId());
        assertEquals(testWord1, foundCombination.getWord1());
        assertEquals(testWord2, foundCombination.getWord2());
        assertEquals(testResult, foundCombination.getResult());
    }

    @Test
    public void getCombination_newCombination_success() {
        Word testWord1 = new Word("Water");
        Word testWord2 = new Word("Fire");
        Word testResult = new Word("Steam");

        Combination testCombination = new Combination(testWord1, testWord2, testResult);

        Mockito.when(combinationRepository.findByWord1AndWord2(testWord1, testWord2)).thenReturn(null);
        Mockito.when(combinationRepository.findByWord1AndWord2(testWord2, testWord1)).thenReturn(null);
        Mockito.when(combinationRepository.saveAndFlush(Mockito.any())).thenReturn(testCombination);
        Mockito.doReturn(testResult.getName()).when(apiService).generateCombinationResult(testWord1.getName(), testWord2.getName());

        Mockito.when(wordService.getWord(testWord1)).thenReturn(testWord1);
        Mockito.when(wordService.getWord(testWord2)).thenReturn(testWord2);
        Mockito.when(wordService.getWord(testResult)).thenReturn(testResult);

        Combination newCombination = combinationService.getCombination(testWord1, testWord2);

        assertEquals(testWord1, newCombination.getWord1());
        assertEquals(testWord2, newCombination.getWord2());
        assertEquals(testResult, newCombination.getResult());
    }

    @Test
    void createCombination_whenResultWordFirstTimeSeen_success() throws Exception {
        Word word1 = new Word("earthquake", 4, 0.07);
        Word word2 = new Word("volcano", 3, 0.11);
        Word resultWord = new Word("apocalypse", 5, (double) 1 / (1L << 5));
        Combination expectedCombination = new Combination(word1, word2, resultWord);

        Mockito.when(wordService.saveWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.doReturn(new Word("apocalypse"))
                .when(combinationService).generateCombinationResult(word1, word2);
        Mockito.doReturn(new Combination(word1, word2, new Word("apocalypse")))
                .when(combinationRepository).saveAndFlush(any());

        Combination actualCombination = combinationService.createCombination(word1, word2);

        assertEquals(expectedCombination, actualCombination);
    }

    @Test
    void generateResultWord_whenResultWordSeenBefore_updatesDepthAndScore() throws Exception {
        Word word1 = new Word("Earthquake", 4, 0.07);
        Word word2 = new Word("Volcano", 3, 0.11);
        Word oldResultWord = new Word("Apocalypse", 6, (double) 1 / (1L << 6));
        Word updatedResultWord = new Word("Apocalypse", 5, (double) 1 / (1L << 5) + (double) 1 / (1L << 6));
        Combination expectedCombination = new Combination(word1, word2, updatedResultWord);
        Mockito.when(wordService.saveWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(wordService.getWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.doReturn(new Word("Apocalypse"))
                .when(combinationService).generateCombinationResult(word1, word2);
        Mockito.doReturn(new Combination(word1, word2, oldResultWord))
                .when(combinationRepository).saveAndFlush(any());

        Combination actualCombination = combinationService.createCombination(word1, word2);

        assertEquals(expectedCombination, actualCombination);
    }

    @Test
    void makeCombinations_oneCombination_success() {
        Word word1 = new Word("water");
        Word word2 = new Word("fire");
        Word resultWord = new Word("steam", 1, (double) 1 / (1L << 1));

        ArrayList<Word> expectedResultList = new ArrayList<>(Arrays.asList(word1, word2, resultWord));
        ArrayList<Word> actualResultList = new ArrayList<>(Arrays.asList(word1, word2));

        Mockito.when(wordService.saveWord(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(wordService.getRandomWord()).thenReturn(word1, word2);

        Mockito.doThrow(new CombinationNotFoundException(word1.getName(), word2.getName()))
                .when(combinationService).findCombination(word1, word2);

        Mockito.doAnswer(new Answer() {
                    public Object answer(InvocationOnMock invocation) {
                        actualResultList.add(resultWord);
                        return new Combination(word1, word2, resultWord);
                    }
                })
                .when(combinationService).createCombination(word1, word2);

        combinationService.makeCombinations(1);

        assertEquals(expectedResultList, actualResultList);
    }
}
