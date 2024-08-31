package antifraud.utils;

import antifraud.dto.TransactionDTO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class TransactionDeserializer extends JsonDeserializer<TransactionDTO> {

    @Override
    public TransactionDTO deserialize(JsonParser parser, DeserializationContext context) throws JsonParseException, IOException {
        try {
            JsonNode node = parser.getCodec().readTree(parser);
            return new TransactionDTO(node.get("amount").asLong(), node.get("ip").asText(), node.get("number").asText(),
                    node.get("region").asText(), node.get("date").asText());
        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionDTO(0L, null, null, null, null);
        }
    }
}
