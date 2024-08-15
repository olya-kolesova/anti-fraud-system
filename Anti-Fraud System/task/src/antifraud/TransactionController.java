package antifraud;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@RestController
public class TransactionController {

    private AppUserRepository repository;
    private PasswordEncoder passwordEncoder;

    public TransactionController(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/api/auth/user")
    public @ResponseBody ResponseEntity<Object> authenticate(@Valid @RequestBody RegistrationRequest request) {
        if (repository.findAppUserByUsername(request.username()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        AppUser appUser = new AppUser();
        appUser.setName(request.name());
        appUser.setUsername(request.username());
        appUser.setPassword(request.password());
        appUser.setAuthority("ROLE_USER");
        repository.save(appUser);
        AppUser repositoryUser = repository.findAppUserByUsername(request.username())
                .orElseThrow(() -> new NoSuchElementException("Not found"));
        Map<String, Object> user = new HashMap<>();
        user.put("id", repositoryUser.getId());
        user.put("name", repositoryUser.getName());
        user.put("username", repositoryUser.getUsername());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
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



}
