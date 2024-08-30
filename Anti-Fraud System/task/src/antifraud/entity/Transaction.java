package antifraud.entity;

import antifraud.utils.TransactionDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
@Entity
@JsonDeserialize(using = TransactionDeserializer.class)
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    @Min(1)
    @NotNull
    private Long amount;

    @NotEmpty
    private String ip;
    @NotEmpty
    private String number;
    @NotEmpty
    private String region;

    private LocalDateTime date;

    public enum Region {
        EAP ("EAP"),
        ECA ("ECA"),
        HIC ("HIC"),
        LAC ("LAC"),
        MENA ("MENA"),
        SA("SA"),
        SSA ("SSA");

        private final String label;
        Region(String label) {
            this.label = label;
        }

        String getLabel() {
            return label;
        }
    }

    private String result;

    private String info;

    public enum Result {
        ALLOWED,
        MANUAL_PROCESSING,
        PROHIBITED
    }

    private List<String> causes = new ArrayList<>();


    public Transaction() {

    }

    public Transaction(String ip, String number, Long amount, String region, String date) throws DateTimeParseException,
            IllegalArgumentException {
        this.ip = ip;
        this.number = number;
        this.amount = amount;
        this.region = Region.valueOf(region).getLabel();
        this.date = LocalDateTime.parse(date);
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) throws IllegalArgumentException {
        this.region = Region.valueOf(region).getLabel();

    }

    public void setResult(Result result) {
        this.result = switch (result) {
            case ALLOWED -> "ALLOWED";
            case MANUAL_PROCESSING -> "MANUAL_PROCESSING";
            case PROHIBITED -> "PROHIBITED";
        };
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(String date) throws DateTimeParseException {
        this.date = LocalDateTime.parse(date);
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

    public void addCause(String cause) {
        causes.add(cause);
    }

    public List<String> getCauses() {
        return causes;
    }

}
