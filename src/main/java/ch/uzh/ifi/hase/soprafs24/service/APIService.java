package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class APIService {

    public String generateCombinationResult(String word1, String word2) {
        return getRandomWord();  // replace with actual llm api call
    }

    public String getRandomWord() {
        String apiUrl = "https://random-word-api.herokuapp.com/word";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, String.class);

        return response.getBody();
    }
}
