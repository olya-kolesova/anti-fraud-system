package antifraud;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;

import java.io.IOException;

public class TransactionDeserializer extends JsonDeserializer<Transaction> {

    @Override
    public Transaction deserialize(JsonParser parser, DeserializationContext context) throws JsonParseException, IOException {
        try {
            JsonNode node = parser.getCodec().readTree(parser);
            return new Transaction(node.get("amount").asLong());
        } catch (Exception e) {
            e.printStackTrace();
            return new Transaction(0L);
        }
    }
}
