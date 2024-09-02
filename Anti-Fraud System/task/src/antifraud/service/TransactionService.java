package antifraud.service;

import antifraud.dto.TransactionDTO;
import antifraud.entity.Transaction;
import antifraud.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionService {

    private final IpService ipService;

    private final StolenCardService stolenCardService;

    private final ModelMapper modelMapper;

    private final TransactionRepository repository;


    public TransactionService(IpService ipService, StolenCardService stolenCardService, ModelMapper modelMapper, TransactionRepository repository) {
        this.ipService = ipService;
        this.stolenCardService = stolenCardService;
        this.modelMapper = modelMapper;
        this.repository = repository;
    }

    public Transaction getMoney(Transaction transaction) throws Exception {
        checkIp(transaction);
        checkNumber(transaction);
        checkRegion(transaction);
        checkAmount(transaction);
        String info = processCauses(transaction.getCauses());
        transaction.setInfo(info);
        repository.save(transaction);
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

    public void checkRegion(Transaction transaction) {
        LocalDateTime dateTo = transaction.getDate();
        System.out.println(dateTo);
        LocalDateTime dateFrom = transaction.getDate().minusHours(1);
        System.out.println(dateFrom);
        List<Transaction> sameTransactions = repository.getAllTransactionsByAmountIpNumberWithinHour(dateFrom, dateTo);
        sameTransactions.stream().map(Transaction::getRegion).forEach(System.out::println);
        long repetition = sameTransactions.stream().map(Transaction::getRegion).distinct().count();
        System.out.println(repetition);
        if (repetition >= 2) {
            transaction.addCause("region-correlation");
            if (repetition == 2) {
                transaction.setResult(Transaction.Result.MANUAL_PROCESSING);
            }
        }
    }





    public void checkAmount(Transaction transaction) {
        if (transaction.getAmount() <= 200) {
            if (transaction.getCauses().isEmpty()) {
                transaction.setResult(Transaction.Result.ALLOWED);
                transaction.setInfo("none");
            }
        } else if (transaction.getAmount() <= 1500 || transaction.getResult().equals("MANUAL_PROCESSING")) {
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

    public Transaction convertDtoToTransaction(TransactionDTO transactionDTO) throws IllegalArgumentException,
            EnumConstantNotPresentException, DateTimeParseException {
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        transaction.setDate(transactionDTO.getDate());
        return transaction;
    }

}
