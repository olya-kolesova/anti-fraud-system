package antifraud.service;

import antifraud.dto.TransactionDTO;
import antifraud.entity.Transaction;
import antifraud.repository.TransactionRepository;
import antifraud.utils.TransactionBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final IpService ipService;

    private final StolenCardService stolenCardService;

    private final TransactionRepository repository;


    public TransactionService(IpService ipService, StolenCardService stolenCardService, ModelMapper modelMapper, TransactionRepository repository) {
        this.ipService = ipService;
        this.stolenCardService = stolenCardService;
        this.repository = repository;
    }

    public Transaction getMoney(TransactionDTO transactionDto) throws Exception {
        TransactionBuilder builder = new TransactionBuilder();
        checkIp(transactionDto, builder);
        checkNumber(transactionDto, builder);
        checkRegion(transactionDto, builder);
        checkIpCorrelation(transactionDto, builder);
        checkAmount(transactionDto, builder);
        String info = processCauses(builder.getCauses());
        builder.setInfo(info);
        Transaction transaction = builder.build();
        repository.save(transaction);
        return transaction;
    }

    public void checkIp(TransactionDTO transactionDto, TransactionBuilder builder) throws Exception {
        if (ipService.validateIp(transactionDto.getIp())) {
            if (ipService.isIpPresent(transactionDto.getIp())) {
                builder.addCause("ip");
                builder.setResult("PROHIBITED");
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
        LocalDateTime dateFrom = transaction.getDate().minusHours(1);
        List<Transaction> sameTransactions = repository.getTransactionsByNumberAmountIpWithinHour(transaction.getNumber(), transaction.getIp(), dateFrom, dateTo);
        sameTransactions.add(transaction);
        long repetitionRegion = sameTransactions.stream().map(Transaction::getRegion).distinct().count();
        if (repetitionRegion >= 3) {
            if (repetitionRegion == 3 && transaction.getCauses().isEmpty()) {
                transaction.setResult(Transaction.Result.MANUAL_PROCESSING);
            } else {
                transaction.setResult(Transaction.Result.PROHIBITED);
            }
            transaction.addCause("region-correlation");
        }
    }

    public void checkIpCorrelation(Transaction transaction) {
        LocalDateTime dateTo = transaction.getDate();
        LocalDateTime dateFrom = transaction.getDate().minusHours(1);
        List<Transaction> sameTransactions = repository.getTransactionsByNumberAmountRegionWithinHour(transaction.getNumber(), transaction.getRegion(), dateFrom, dateTo);
        sameTransactions.add(transaction);
        long repetitionIp = sameTransactions.stream().map(Transaction::getIp).distinct().count();
        if (repetitionIp >= 3) {
            if ((repetitionIp == 3) && (transaction.getCauses().isEmpty() || transaction.getResult().equals("MANUAL_PROCESSING"))) {
                transaction.setResult(Transaction.Result.MANUAL_PROCESSING);
            } else {
                transaction.setResult(Transaction.Result.PROHIBITED);
            }
            transaction.addCause("ip-correlation");
        }
    }


    public void checkAmount(Transaction transaction) {
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

//    public Transaction convertDtoToTransaction(TransactionDTO transactionDTO) throws IllegalArgumentException,
//            EnumConstantNotPresentException, DateTimeParseException {
//        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
//        transaction.setDate(transactionDTO.getDate());
//        return transaction;
//    }


    public List<Transaction> getHistoryByNumber(String number) {
        return repository.findAllByNumberOrderById(number);
    }

}
