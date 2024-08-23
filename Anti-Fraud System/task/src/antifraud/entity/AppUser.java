package antifraud.entity;

import antifraud.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;



@Entity
public class AppUser {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String username;
    private String password;
    private Role role;
    private String authority;
    private boolean nonLocked;

    public AppUser() {};


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(Role role) {
        this.authority = switch(role) {
            case MERCHANT -> "MERCHANT";
            case SUPPORT -> "SUPPORT";
            case ADMINISTRATOR -> "ADMINISTRATOR";
        };

    }

    public boolean isNonLocked() {
        return nonLocked;
    }

    public void setNonLocked(boolean lock) {
        this.nonLocked = lock;
    }





}
