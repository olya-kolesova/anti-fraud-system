package antifraud.service;

import antifraud.entity.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    public record TransactionRecord(String result, String info) {}

    public TransactionRecord getMoney(Transaction transaction) {
        if (transaction.getAmount() <= 200) {
            transaction.setResult(Transaction.Result.ALLOWED);
        } else if (transaction.getAmount() <= 1500) {
            transaction.setResult(Transaction.Result.MANUAL_PROCESSING);
        } else {
            transaction.setResult(Transaction.Result.PROHIBITED);
        }
        return new TransactionRecord(transaction.getResult(), transaction.getInfo());
    }

}
