package ch.uzh.ifi.hase.soprafs24.service;

import javassist.compiler.ast.Symbol;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

@Service
public class APIService {
    public String generateCombinationResult(String word1, String word2) {
        return getVertexAIWord(word1, word2);
    }

    public String getAwanLLMWord(String word1, String word2) throws JSONException {
        String model = "Meta-Llama-3-8B-Instruct";
        String systemMessage = "Reply only with the element that comes by combining two elements using the logic on the examples below.\\nExamples:\\n\\nearth + water\\nplant\\n\\nearth + lava\\nstone\\n\\n\\nearth + island\\ncontinent\\n\\nwater + water\\nlake\\n\\n\\nfire + fire\\nvolcano";
        String userMessage = String.format("%s + %s", word1, word2);
        String requestBody = String.format("{\"model\": \"%s\", \"messages\": [{\"role\":\"system\",\"content\": \"%s\"}, {\"role\": \"user\",\"content\": \"%s\"}]}", model, systemMessage, userMessage);
        String apiUrl = "https://api.awanllm.com/v1/chat/completions";

        Dotenv dotenv = Dotenv.configure().directory("src/main/resources").load();
        String apiKey = dotenv.get("AWAN_LLM_KEY");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        System.out.println(httpEntity);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, String.class);

        JSONObject jsonResponse = new JSONObject(response.getBody());
        return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").get("content").toString();
    }

    public String getVertexAIWord(String word1, String word2) throws JSONException {
        String apiUrl = "https://us-central1-aiplatform.googleapis.com/v1/projects/sopra-fs24-rshanm-server/locations/us-central1/publishers/google/models/text-bison:predict";
        JSONObject requestBody = new JSONObject();
        JSONArray instancesArray = new JSONArray();
        JSONObject instance = new JSONObject();
        String promptText = String.format("Reply only with the element that comes by combining two elements using the logic on the examples below: \\nExamples:\\n\\nEarth + Water\\nPlant\\n\\nEarth + Lava\\nStone\\n\\n\\nEarth + Island\\nContinent\\n\\nWater + Water\\nLake\\\\n\\nFire + Fire\\nVolcano. The words I give you now are: %s + %s", word1, word2);
        instance.put("prompt",promptText);
        instancesArray.put(instance);
        requestBody.put("instances", instancesArray);
        JSONObject parameters = new JSONObject();
        parameters.put("temperature", 0);
        parameters.put("maxOutputTokens", 100);
        parameters.put("topP", 0.5);
        parameters.put("topK", 1);
        requestBody.put("parameters", parameters);

        Dotenv dotenv = Dotenv.configure().directory("src/main/resources").load();
        String apiKey = dotenv.get("GOOGLE_CLOUD_KEY");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, String.class);
        System.out.println(response.getBody());

        JSONObject jsonResponse = new JSONObject(response.getBody());
        String content = jsonResponse.getJSONArray("predictions").getJSONObject(0).getString("content");
        content = content.replace(" ", "");
        System.out.println(content);
        return content;
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
