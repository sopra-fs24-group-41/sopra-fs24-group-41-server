package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class APIServiceTest {

    @InjectMocks
    private APIService apiService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getVertexAIWord_success() throws IOException {
        String result = apiService.getVertexAIWord("water", "fire");

        assertNotNull(result);
        assertNotEquals("", result);
        System.out.println(result);
    }

    @Test
    void getRandomWord_success() {
        String result = apiService.getRandomWord();

        assertNotNull(result);
        assertNotEquals("", result);
    }

    @Test
    void generateCombination_success() {
        String result = apiService.generateCombinationResult("water", "fire");

        assertNotNull(result);
        assertNotEquals("", result);
    }
}