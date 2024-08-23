package antifraud.controller;

import antifraud.service.AppUserService;
import antifraud.entity.Transaction;
import antifraud.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;



@RestController
public class TransactionController {

    @Autowired
    private final TransactionService service;


    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/api/antifraud/transaction")
    public @ResponseBody ResponseEntity<Object> requestTransaction(@Valid @RequestBody Transaction transaction) {
        return new ResponseEntity<>(service.getMoney(transaction), HttpStatus.OK);
    }


}
