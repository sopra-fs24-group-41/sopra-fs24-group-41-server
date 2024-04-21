package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordGenerationService {

    private final WordService wordService;

    private final CombinationService combinationService;

    @Autowired
    public WordGenerationService(WordService wordService, CombinationService combinationService) {
        this.wordService = wordService;
        this.combinationService = combinationService;
    }

    public void generateDatabase(int numberOfSteps) {
        return;
    }

    public String getTargetWord(double difficultyScore) {
        return "";
    }
}
