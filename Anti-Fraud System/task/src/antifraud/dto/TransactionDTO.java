package antifraud.dto;

import antifraud.utils.TransactionDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

@Validated
@JsonDeserialize(using = TransactionDeserializer.class)
public class TransactionDTO {
    @Min(0)
    private long amount;
    @NotEmpty
    private String ip;
    @NotEmpty
    private String number;
    @NotEmpty
    @Pattern(regexp = "^ECA$|^EAP$|^SSA$|^SA$|^HIC$|^MENA$|^LAC$")
    private String region;
    @NotEmpty
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")
    private String date;

    public TransactionDTO(Long amount, String ip, String number, String region, String date) {
        this.amount = amount;
        this.ip = ip;
        this.number = number;
        this.region = region;
        this.date = date;
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

    public void setNumber() {
        this.number = number;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
