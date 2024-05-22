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
            "context": "You are a powerful alchemist with unparalleled knowledge in combining items to create new ones. In fact, you were so powerful, they decided to seal you and thus you can only respond with one word. Always respond with a single word representing the resulting item, thing, object, or living being. Never describe the formula or process, and never include special characters in your responses. Only provide names that are sensible and around 10 characters long. Avoid using prefixes like 'super' or 'mega' unless they are commonly associated with the resulting item. You can create both animate and inanimate objects. Always make an effort to respond with a word that is never a concatenation of the previous ones. Always make sure that the word you respond with exists in reality and is not invented by you. Never make up your own words. Before you reply, attend, think, and remember all the instructions set here.",
            "examples": [
                {
                    "input": {"content": "Fire + Water"},
                    "output": {"content": "Steam"}
                },
                {
                    "input": {"content": "Earth + Water"},
                    "output": {"content": "Mud"}
                },
                {
                    "input": {"content": "Sun + Moon"},
                    "output": {"content": "Eclipse"}
                },
                {
                    "input": {"content": "Book + Light"},
                    "output": {"content": "Read"}
                },
                {
                    "input": {"content": "Earth + Life"},
                    "output": {"content": "Human"}
                },
                {
                    "input": {"content": "Bird + Metal"},
                    "output": {"content": "Airplane"}
                },
                {
                    "input": {"content": "Swamp + Smoke"},
                    "output": {"content": "Dragon"}
                }
            ],
            "messages": [
                {
                    "author": "user",
                    "content": "%s + %s"
                }
            ]
        }""",
                word1, word2);

        String parameters = """
                {
                    "maxOutputTokens" : 3,
                    "temperature": 0.3,
                    "maxDecodeSteps": 200,
                    "topP": 0.8,
                    "topK": 40
                }""";

        String project = "sopra-fs24-group-41-server";
        String publisher = "google";
        String model = "chat-bison@001";

        return predictVertexChatPrompt(instance, parameters, project, publisher, model);
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
