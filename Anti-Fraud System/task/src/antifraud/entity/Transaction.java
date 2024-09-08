package antifraud.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDateTime;


@Entity
@Validated
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Min(1)
    private Long amount;
    @Column
    private String ip;
    @Column
    private String number;
    @Column
    private String region;
    @Column
    private LocalDateTime date;

    @Column
    private String result;
    @Column
    private String info;

    @Column
    private String feedback;


    public Transaction(Long amount, String ip, String number, String region, LocalDateTime date, String result, String feedback, String info) {
        this.amount = amount;
        this.ip = ip;
        this.number = number;
        this.region = region;
        this.date = date;
        this.result = result;
        this.feedback = feedback;
        this.info = info;
    }

    public Long getId() {
        return id;
    }

    public Long getAmount() {
        return amount;
    }


    public String getIp() {
        return ip;
    }


    public String getNumber() {
        return number;
    }


    public String getRegion() {
        return region;
    }

    public LocalDateTime getDate() {
        return date;
    }


    public String getResult() {
        return result;
    }

    public String getInfo() {
        return info;
    }

    public String getFeedback() {
        return feedback;
    }
}
