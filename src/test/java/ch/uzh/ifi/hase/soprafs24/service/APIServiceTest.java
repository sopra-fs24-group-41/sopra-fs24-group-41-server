package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class APIServiceTest {

    @InjectMocks
    private APIService apiService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getRandomWord_success() {
        String responseBody = apiService.getRandomWord();

        assertNotNull(responseBody);
    }

    @Test
    public void generateCombination_success() {
        String result = apiService.generateCombinationResult("water", "fire");

        assertNotNull(result);
        assertNotEquals("", result);
        assertFalse(result.contains(" "));
    }
}