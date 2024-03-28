package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CombinationServiceTest {

    @Mock
    private CombinationRepository combinationRepository;

    @Mock
    private APIService apiService;

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

        Mockito.doReturn(testCombination).when(combinationService).findCombination(testWord1, testWord2);

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

        Mockito.doThrow(new CombinationNotFoundException(testWord1.getName(), testWord2.getName())).when(combinationService).findCombination(testWord1, testWord2);
        Mockito.doReturn(testCombination).when(combinationRepository).save(Mockito.any());
        Mockito.doReturn(testResult.getName()).when(apiService).generateCombinationResult(testWord1.getName(), testWord2.getName());

        Combination newCombination = combinationService.getCombination(testWord1, testWord2);

        assertEquals(testWord1, newCombination.getWord1());
        assertEquals(testWord2, newCombination.getWord2());
        assertEquals(testResult, newCombination.getResult());
    }
}
