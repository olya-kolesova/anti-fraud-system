package antifraud;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;


@Validated
@RestController
public class TransactionController {

    @PostMapping("/api/antifraud/transaction")

    public @ResponseBody ResponseEntity<Object> requestTransaction(@Valid @RequestBody Transaction transaction) {
        if (transaction.getAmount() <= 200) {
            transaction.setResult(Transaction.Result.ALLOWED);
            return getResponseForAmount(transaction);
        } else if (transaction.getAmount() <= 1500) {
            transaction.setResult(Transaction.Result.MANUAL_PROCESSING);
            return getResponseForAmount(transaction);
        } else {
            transaction.setResult(Transaction.Result.PROHIBITED);
            return getResponseForAmount(transaction);
        }

    }

    private ResponseEntity<Object> getResponseForAmount(Transaction transaction) {
        Map<String, Object> body = new HashMap<>();
        body.put("result", transaction.getResult());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
