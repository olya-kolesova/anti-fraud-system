package antifraud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
public class TransactionController {

    @Autowired
    private final AppUserService service;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public TransactionController(AppUserService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/auth/user")
    public @ResponseBody ResponseEntity<Object> authenticate(@Valid @RequestBody RegistrationRequest request) throws JsonProcessingException {
        if (service.isUserPresent(request.username().toLowerCase())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        AppUser appUser = new AppUser();
        appUser.setName(request.name());
        appUser.setUsername(request.username());
        appUser.setPassword(passwordEncoder.encode(request.password()));
        appUser.setAuthority("ROLE_USER");
        service.saveUser(appUser);
        AppUserDTO user = service.findAppUserDTOByUsername(request.username());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
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


    @PostMapping("/api/antifraud/transaction")
    public @ResponseBody ResponseEntity<Object> requestTransaction(@Valid @RequestBody Transaction transaction) {
        if (transaction.getAmount() <= 200) {
            transaction.setResult(Transaction.Result.ALLOWED);
            return getResponseForAmount(transaction);
        } else if (transaction.getAmount() <= 1500) {
            transaction.setResult(Transaction.Result.MANUAL_PROCESSING);
            return getResponseForAmount(transaction);
        } else {
            transaction.setResult(Transaction.Result.PROHIBITED);
            return getResponseForAmount(transaction);
        }

    }

    private ResponseEntity<Object> getResponseForAmount(Transaction transaction) {
        Map<String, Object> body = new HashMap<>();
        body.put("result", transaction.getResult());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Validated
    public record RegistrationRequest(@NotEmpty String name, @NotEmpty String username, @NotEmpty String password) {}

    public record DeleteResponse(String username, String status) {}

}
