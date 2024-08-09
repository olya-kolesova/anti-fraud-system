package antifraud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TransactionController {

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<Object> requestTransaction(@RequestBody Transaction transaction) {
        if (transaction.getAmount() < 0) {
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
//        } else if (transaction.getAmount() <= 200) {
//
//            return new ResponseEntity<Transaction>()
        } else if (transaction.getAmount() <= 200) {
            transaction.setResult(Transaction.Result.ALLOWED);
            getResponseForAmount(transaction);
        } else if()

    }

    private ResponseEntity<Object> getResponseForAmount(Transaction transaction) {
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transaction);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
