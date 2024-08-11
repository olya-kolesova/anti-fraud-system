package antifraud;



import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


import java.io.Serializable;

@JsonDeserialize(using = TransactionDeserializer.class)
public class Transaction {

    private String result;

    public enum Result {
        ALLOWED,
        MANUAL_PROCESSING,
        PROHIBITED
    }
    @Min(1)
    @NotNull
    private Long amount;

    public Transaction() {

    }

    public Transaction(Long amount) {
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setResult(Result result) {
        this.result = switch (result) {
            case ALLOWED -> "ALLOWED";
            case MANUAL_PROCESSING -> "MANUAL_PROCESSING";
            case PROHIBITED -> "PROHIBITED";
        };
    }

    public String getResult() {
        return result;
    }


}
