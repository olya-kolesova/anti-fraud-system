package antifraud.entity;

import antifraud.utils.TransactionDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@JsonDeserialize(using = TransactionDeserializer.class)
public class Transaction {


    @Min(1)
    @NotNull
    private Long amount;


    private String ip;
    private String number;

    private String result;

    private String info;

    public enum Result {
        ALLOWED,
        MANUAL_PROCESSING,
        PROHIBITED
    }


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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


}
