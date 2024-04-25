package ch.uzh.ifi.hase.soprafs24.service;

import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.PredictResponse;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class APIService {
    public String generateCombinationResult(String word1, String word2) {
        try {
            return getVertexAIWord(word1, word2);
        }
        catch (Exception e) {
            return word1;
        }
    }

    //This code works as long as you use a working environment variable called GOOGLE_APPLICATION_CREDENTIALS
    public String getVertexAIWord(String word1, String word2) throws IOException {
        String instance = String.format("""
                        {
                           "context":  "You are an AI that only returns the combination of two given words. Reply only with the element that comes by combining two elements using the logic on the examples below.

                        .",
                           "examples": [ {\s
                               "input": {"content": "Earth + Water"},
                               "output": {"content": "Steam"}
                            },
                            {\s
                               "input": {"content": "Earth + Lava"},
                               "output": {"content": "Stone"}
                            }],
                           "messages": [
                            {\s
                               "author": "user",
                               "content": "%s + %s"
                            }]
                        }""",
                        word1, word2);
        String parameters ="""
                        {
                          "temperature": 0.3,
                          "maxDecodeSteps": 200,
                          "topP": 0.8,
                          "topK": 40
                        }""";
        String project = "sopra-fs24-group-41-server"; //Unsure if this matters actually, for the Credential confirmation
        String publisher = "google";
        String model = "chat-bison@001"; //Unsure about this one, could be changed.

        return predictVertexChatPrompt(instance, parameters, project, publisher, model);
    }

    public String getAwanLLMWord(String word1, String word2) {
        String requestBody = buildAwanRequest(word1, word2);
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
        return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                .get("content").toString();
    }

    private static String buildAwanRequest(String word1, String word2) {
        String model = "Meta-Llama-3-8B-Instruct";
        String systemMessage = "Reply only with the element that comes by combining two elements using the logic on the examples below.\\nExamples:\\n\\nearth + water\\nplant\\n\\nearth + lava\\nstone\\n\\n\\nearth + island\\ncontinent\\n\\nwater + water\\nlake\\n\\n\\nfire + fire\\nvolcano";
        String userMessage = String.format("%s + %s", word1, word2);
        return String.format("""
                {"model": "%s",\s
                "messages": [{"role":"system","content": "%s"},\s
                "{"role": "user","content": "%s"}]}
                """,
                model, systemMessage, userMessage);
    }

    public String getRandomWord() {
        String apiUrl = "https://random-word-api.herokuapp.com/word";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, String.class);

        return response.getBody();
    }

    private String predictVertexChatPrompt(String instance, String parameters, String project, String publisher, String model) throws IOException {
        PredictionServiceSettings predictionServiceSettings = PredictionServiceSettings.newBuilder().setEndpoint("europe-west4-aiplatform.googleapis.com:443").build();

        String content = null;

        try (PredictionServiceClient predictionServiceClient = PredictionServiceClient.create(predictionServiceSettings)) {
            String location = "europe-west4";
            final EndpointName endpointName = EndpointName.ofProjectLocationPublisherModelName(project, location, publisher, model);

            Value.Builder instanceValue = Value.newBuilder();
            JsonFormat.parser().merge(instance, instanceValue);
            List<Value> instances = new ArrayList<>();
            instances.add(instanceValue.build());

            Value.Builder parameterValueBuilder = Value.newBuilder();
            JsonFormat.parser().merge(parameters, parameterValueBuilder);
            Value parameterValue = parameterValueBuilder.build();

            PredictResponse predictResponse = predictionServiceClient.predict(endpointName, instances, parameterValue);

            List<Value> predictionsList = predictResponse.getPredictionsList();
            if (!predictionsList.isEmpty()) {
                Struct predictionStruct = predictionsList.get(0).getStructValue();
                Value candidatesValue = predictionStruct.getFieldsOrThrow("candidates");
                Struct firstCandidate = candidatesValue.getListValue().getValues(0).getStructValue();
                content = firstCandidate.getFieldsOrThrow("content").getStringValue();
            }
        }
        return content;
    }
}
