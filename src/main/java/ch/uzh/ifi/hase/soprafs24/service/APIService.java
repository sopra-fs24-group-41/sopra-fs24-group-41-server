package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;


@Service
public class APIService {

    public String generateCombinationResult(String word1, String word2) {
        return getRandomWord();  // replace with actual llm api call
    }

    public String getMistralWord(String word1, String word2) {
        String requestBody = constructRequestBody(word1, word2);
        String apiUrl = "https://api.awanllm.com/v1/chat/completions";
//        String apiKey = System.getenv("MISTRAL_API_KEY");
        String apiKey = "INSERT KEY HERE";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, String.class);

        JSONObject jsonResponse = new JSONObject(response.getBody());
        return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").get("content").toString();
    }

    private static String constructRequestBody(String word1, String word2) {
        String model = "Meta-Llama-3-8B-Instruct";
        String systemMessage = "Reply only with the element that comes by combining two elements using the logic on the examples below.\\nExamples:\\n\\nearth + water\\nplant\\n\\nearth + lava\\nstone\\n\\n\\nearth + island\\ncontinent\\n\\nwater + water\\nlake\\n\\n\\nfire + fire\\nvolcano";
        String userMessage = String.format("%s + %s", word1, word2);
        String requestBody = String.format("{\"model\": \"%s\", \"messages\": [{\"role\":\"system\",\"content\": \"%s\"}, {\"role\": \"user\",\"content\": \"%s\"}]}", model, systemMessage, userMessage);
        return requestBody;
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
