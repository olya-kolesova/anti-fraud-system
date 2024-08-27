package antifraud.service;

import antifraud.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionService {

    private final IpService ipService;

    private final StolenCardService stolenCardService;


    public TransactionService(IpService ipService, StolenCardService stolenCardService) {
        this.ipService = ipService;
        this.stolenCardService = stolenCardService;
    }

    public Transaction getMoney(Transaction transaction) throws Exception {
        checkIp(transaction);
        checkNumber(transaction);
        checkAmmount(transaction);
        String info = processCauses(transaction.getCauses());
        transaction.setInfo(info);
        return transaction;
    }

    public void checkIp(Transaction transaction) throws Exception {
        if (ipService.validateIp(transaction.getIp())) {
            if (ipService.isIpPresent(transaction.getIp())) {
                transaction.addCause("ip");
                transaction.setResult(Transaction.Result.PROHIBITED);
            }
        } else {
            throw new Exception();
        }
    }

    public void checkNumber(Transaction transaction) throws Exception {
        if (stolenCardService.checkCard(transaction.getNumber())) {
            if (stolenCardService.isCardPresent(transaction.getNumber())) {
                transaction.addCause("card-number");
                transaction.setResult(Transaction.Result.PROHIBITED);
            }
        } else {
            throw new Exception();
        }
    }

    public void checkAmmount(Transaction transaction) {
        if (transaction.getAmount() <= 200) {
            if (transaction.getCauses().isEmpty()) {
                transaction.setResult(Transaction.Result.ALLOWED);
                transaction.setInfo("none");
            }
        } else if (transaction.getAmount() <= 1500) {
            if (transaction.getCauses().isEmpty()) {
                transaction.setResult(Transaction.Result.MANUAL_PROCESSING);
                transaction.addCause("amount");
            }
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
            return causes.stream().sorted().collect(Collectors.joining(", "));
        }
    }

}
