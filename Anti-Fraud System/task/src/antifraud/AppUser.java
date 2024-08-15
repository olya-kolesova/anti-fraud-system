package antifraud;

import javax.annotation.processing.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
;
@Entity
public class AppUser {
    private String name;
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;

    public AppUser() {};

    public App
}
