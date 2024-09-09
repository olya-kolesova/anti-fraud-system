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

    private final ModelMapper modelMapper;


    public TransactionService(IpService ipService, StolenCardService stolenCardService, ModelMapper modelMapper, TransactionRepository repository) {
        this.ipService = ipService;
        this.stolenCardService = stolenCardService;
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public Transaction getMoney(TransactionDTO transactionDto) throws Exception {
        TransactionBuilder builder = modelMapper.map(transactionDto, TransactionBuilder.class);
        checkIp(transactionDto, builder);
        checkNumber(transactionDto, builder);
        Transaction transaction = builder.build();
        checkRegion(builder, transaction);
        checkIpCorrelation(builder, transaction);
        checkAmount(transactionDto, builder);
        String info = processCauses(builder.getCauses());
        builder.setInfo(info);
        Transaction transactionFinal = builder.build();
        repository.save(transactionFinal);
        return transactionFinal;
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

    public void checkNumber(TransactionDTO transactionDto, TransactionBuilder builder) throws Exception {
        if (stolenCardService.checkCard(transactionDto.getNumber())) {
            if (stolenCardService.isCardPresent(transactionDto.getNumber())) {
                builder.addCause("card-number");
                builder.setResult("PROHIBITED");
            }
        } else {
            throw new Exception();
        }
    }

    public void checkRegion(TransactionBuilder builder, Transaction transaction) {
        LocalDateTime dateTo = transaction.getDate();
        LocalDateTime dateFrom = transaction.getDate().minusHours(1);
        List<Transaction> sameTransactions = repository.getTransactionsByNumberAmountIpWithinHour(transaction.getNumber(), transaction.getIp(), dateFrom, dateTo);
        sameTransactions.add(transaction);
        long repetitionRegion = sameTransactions.stream().map(Transaction::getRegion).distinct().count();
        if (repetitionRegion >= 3) {
            if (repetitionRegion == 3 && builder.getCauses().isEmpty()) {
                builder.setResult("MANUAL_PROCESSING");
            } else {
                builder.setResult("PROHIBITED");
            }
            builder.addCause("region-correlation");
        }
    }

    public void checkIpCorrelation(TransactionBuilder builder, Transaction transaction) {
        LocalDateTime dateTo = transaction.getDate();
        LocalDateTime dateFrom = transaction.getDate().minusHours(1);
        List<Transaction> sameTransactions = repository.getTransactionsByNumberAmountRegionWithinHour(transaction.getNumber(), transaction.getRegion(), dateFrom, dateTo);
        sameTransactions.add(transaction);
        long repetitionIp = sameTransactions.stream().map(Transaction::getIp).distinct().count();
        if (repetitionIp >= 3) {
            if ((repetitionIp == 3) && (builder.getCauses().isEmpty() || transaction.getResult().equals("MANUAL_PROCESSING"))) {
                builder.setResult("MANUAL_PROCESSING");
            } else {
                builder.setResult("PROHIBITED");
            }
            builder.addCause("ip-correlation");
        }
    }


    public void checkAmount(TransactionDTO transactionDto, TransactionBuilder builder) {
        if (transactionDto.getAmount() <= 200) {
            if (builder.getCauses().isEmpty()) {
                builder.setResult("ALLOWED");
                builder.setInfo("none");
            }
        } else if (transactionDto.getAmount() <= 1500) {
            if (builder.getCauses().isEmpty()) {
                builder.setResult("MANUAL_PROCESSING");
                builder.addCause("amount");
            }
        } else {
            builder.setResult("PROHIBITED");
            builder.addCause("amount");
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
