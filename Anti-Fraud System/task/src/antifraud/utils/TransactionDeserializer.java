package antifraud.utils;

import antifraud.entity.Transaction;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class TransactionDeserializer extends JsonDeserializer<Transaction> {

    @Override
    public Transaction deserialize(JsonParser parser, DeserializationContext context) throws JsonParseException, IOException {
        try {
            JsonNode node = parser.getCodec().readTree(parser);
            return new Transaction(node.get("ip").asText(), node.get("number").asText(), node.get("amount").asLong(),
                    node.get("region").asText(), node.get("date").asText());
        } catch (Exception e) {
            e.printStackTrace();
            return new Transaction(null, null,0L, null, null);
        }
    }
}
