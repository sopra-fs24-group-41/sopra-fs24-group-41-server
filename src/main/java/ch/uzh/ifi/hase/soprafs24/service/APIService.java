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
        String apiUrl = "https://random-words5.p.rapidapi.com/getRandom";
        String apiKey = "d5b9992233mshb61f6d853db1eafp14a0ffjsn62bda1d8c90d";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", "random-words5.p.rapidapi.com");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, String.class);

        return response.getBody();
    }
}
