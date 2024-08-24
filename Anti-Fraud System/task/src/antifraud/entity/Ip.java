package antifraud.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

@Entity
public class Ip {
    @GeneratedValue
    @Column
    private Long id;
    @Column
    private String ip;
}
