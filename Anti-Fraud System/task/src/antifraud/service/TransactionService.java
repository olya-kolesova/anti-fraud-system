package antifraud.service;

import antifraud.entity.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionService {

    public record TransactionRecord(String result, String info) {}

    public TransactionRecord getMoney(Transaction transaction) {
        checkAmmount(transaction);
        String info = processCauses(transaction.getCauses());
        transaction.setInfo(info);
        return new TransactionRecord(transaction.getResult(), transaction.getInfo());
    }

//    public Transaction checkIp(Transaction transaction) {
//        iPService.findByIp(transaction.getIp()).isPresent() {
//
//        }
//    }

    //    public Transaction checkNumber(Transaction transaction) {
//        NumberService.findByNumber(transaction.getIp()).isPresent() {
//
//        }
//    }

    public void checkAmmount(Transaction transaction) {
        if (transaction.getAmount() <= 200) {
            transaction.setResult(Transaction.Result.ALLOWED);
            if (transaction.getCauses().isEmpty()) {
                transaction.setInfo("none");
            }
        } else if (transaction.getAmount() <= 1500) {
            transaction.setResult(Transaction.Result.MANUAL_PROCESSING);
            transaction.addCause("amount");
        } else {
            transaction.setResult(Transaction.Result.PROHIBITED);
            transaction.addCause("amount");
        }
    }




    public String processCauses(List<String> causes) {
        if (causes.isEmpty()) {
            return "none";
        } else if (causes.size() == 1) {
            return causes.get(0);
        } else {
            String causesStr = causes.stream().sorted().filter(x -> causes.indexOf(x) != causes.size() - 1)
                    .collect(Collectors.joining(", "));

            return causesStr.concat(causes.get(causes.size() - 1));
        }
    }

}
