package ch.uzh.ifi.hase.soprafs24.service;
import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.PredictResponse;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//This is example code from the documentation.
// Send a Predict request to a large language model to test a chat prompt
public class Vertex {

    public static void main(String[] args) throws IOException {
        // TODO(developer): Replace these variables before running the sample.
        String instance =
                "{\n"
                        + "   \"context\":  \"You are an AI that only returns the combination of two given words. Reply only with the element that comes by combining two elements using the logic on the examples below.\n" +
                        "\n.\",\n"
                        + "   \"examples\": [ { \n"
                        + "       \"input\": {\"content\": \"Earth + Water\"},\n"
                        + "       \"output\": {\"content\": \"Steam\"}\n"
                        + "    },\n"
                        + "    { \n"
                        + "       \"input\": {\"content\": \"Earth + Lava\"},\n"
                        + "       \"output\": {\"content\": \"Stone\"}\n"
                        + "    }],\n"
                        + "   \"messages\": [\n"
                        + "    { \n"
                        + "       \"author\": \"user\",\n"
                        + "       \"content\": \"Water + Fire\"\n"
                        + "    }]\n"
                        + "}";
        String parameters =
                "{\n"
                        + "  \"temperature\": 0.3,\n"
                        + "  \"maxDecodeSteps\": 200,\n"
                        + "  \"topP\": 0.8,\n"
                        + "  \"topK\": 40\n"
                        + "}";
        String project = "sopra-fs24-rshanm-server";
        String publisher = "google";
        String model = "chat-bison@001";

        String output = predictChatPrompt(instance, parameters, project, publisher, model);
        System.out.println(output);
    }

    static String predictChatPrompt(
            String instance, String parameters, String project, String publisher, String model)
            throws IOException {
        PredictionServiceSettings predictionServiceSettings =
                PredictionServiceSettings.newBuilder()
                        .setEndpoint("us-central1-aiplatform.googleapis.com:443")
                        .build();

        String content = null;

        try (PredictionServiceClient predictionServiceClient =
                     PredictionServiceClient.create(predictionServiceSettings)) {
            String location = "us-central1";
            final EndpointName endpointName =
                    EndpointName.ofProjectLocationPublisherModelName(project, location, publisher, model);

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
