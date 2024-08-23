package antifraud.controller;

import antifraud.Role;
import antifraud.dto.AppUserDTO;
import antifraud.entity.AppUser;
import antifraud.service.AppUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private final AppUserService service;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public UserController(AppUserService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @Validated
    public record RegistrationRequest(@NotEmpty String name, @NotEmpty String username, @NotEmpty String password) {}
    @Validated
    public record RoleAssignmentRequest(@NotEmpty String username, @NotEmpty String role) {}
    @Validated
    public record AccessRequest(@NotEmpty String username, @NotEmpty String operation) {}
    public record DeleteResponse(String username, String status) {}



    @PostMapping("/api/auth/user")
    public @ResponseBody ResponseEntity<Object> authenticate(@Valid @RequestBody RegistrationRequest request) throws JsonProcessingException {
        if (service.isUserPresent(request.username().toLowerCase())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        AppUser appUser = new AppUser();
        appUser.setName(request.name());
        appUser.setUsername(request.username());
        appUser.setPassword(passwordEncoder.encode(request.password()));
        if (service.findAppUserDTOByOrder().isEmpty()) {
            appUser.setAuthority(Role.ADMINISTRATOR);
            appUser.setNonLocked(true);
        } else {
            appUser.setAuthority(Role.MERCHANT);
            appUser.setNonLocked(false);
        }

        service.saveUser(appUser);

        AppUser user = service.findAppUserByUsername(request.username());
        AppUserDTO userDTO = service.convertAppUserToDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }


    @GetMapping("/api/auth/list")
    public ResponseEntity<List<AppUserDTO>> getUserList() {
        return new ResponseEntity<>(service.findAppUserDTOByOrder(), HttpStatus.OK) ;
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<Object> deleteUser(@PathVariable String username) {
        if (!(service.isUserPresent(username))) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }

        service.deleteAppUserByUsername(username);
        DeleteResponse deleteResponse = new DeleteResponse(username, "Deleted successfully!");
        return new ResponseEntity<>(deleteResponse, HttpStatus.OK);

    }

    @PutMapping("/api/auth/role")
    public ResponseEntity<Object> assignRole(@RequestBody RoleAssignmentRequest request) {
        if (!(request.role.equals("SUPPORT") || request.role.equals("MERCHANT"))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!(service.isUserPresent(request.username()))) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            try {
                AppUser user = service.findAppUserByUsername(request.username());
                if (user.getAuthority().equals(request.role())) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                } else {
                    user.setAuthority(Role.valueOf(request.role()));
                    service.saveUser(user);
                    AppUserDTO userDTO = service.convertAppUserToDTO(user);
                    return new ResponseEntity<>(userDTO, HttpStatus.OK);
                }
            } catch (UsernameNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<Object> changeAccess(@RequestBody AccessRequest accessRequest) {
        if (accessRequest.operation().equals("UNLOCK") || accessRequest.operation().equals("LOCK")) {
            try {
                AppUser user = service.findAppUserByUsername(accessRequest.username());
                if (user.getAuthority().equals("ADMINISTRATOR")) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    user.setNonLocked(accessRequest.operation.equals(("UNLOCK")));
                }
                service.saveUser(user);
                Map<String, String> status = new HashMap();
                String statusWord = user.isNonLocked() ? "unlocked" : "locked";
                String statusExp = String.format("User %s %s!", user.getUsername(), statusWord);
                status.put("status", statusExp);
                return new ResponseEntity<>(status, HttpStatus.OK);
            } catch (UsernameNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}




