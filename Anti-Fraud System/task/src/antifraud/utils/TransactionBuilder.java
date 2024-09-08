package antifraud.utils;

import antifraud.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionBuilder {

    private Long amount;

    private String ip;

    private String number;

    private String region;

    private LocalDateTime date;

    private String result;

    private String feedback = "";

    private String info;


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

    public enum Result {
        ALLOWED ("ALLOWED"),
        MANUAL_PROCESSING ("MANUAL_PROCESSING"),
        PROHIBITED ("PROHIBITED");

        private final String label;

        Result(String label) {
            this.label = label;
        }

        String getLabel() {
            return label;
        }
    }

    private List<String> causes = new ArrayList<>();

    public TransactionBuilder() {};

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setRegion(String region) throws EnumConstantNotPresentException {
        this.region = Region.valueOf(region).getLabel();
    }

    public void setDate(String date) throws DateTimeParseException {
        this.date = LocalDateTime.parse(date);
    }

    public void setResult(String result) throws EnumConstantNotPresentException {
        this.result = Result.valueOf(result).getLabel();
    }

    public void setFeedback(String feedback) {
        this.feedback = Objects.requireNonNullElse(feedback, "");
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

    public Transaction build() {
        return new Transaction(amount, ip, number, region, date, result, feedback, info);
    }


}
