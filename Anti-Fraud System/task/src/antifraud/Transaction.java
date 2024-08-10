package antifraud;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Transaction {

    private String result;

    public enum Result {
        ALLOWED,
        MANUAL_PROCESSING,
        PROHIBITED
    }
    private long amount;

    public Transaction() {

    }

    public Transaction(long amount) {
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
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
