package antifraud.controller;

import antifraud.entity.Ip;
import antifraud.service.AppUserService;
import antifraud.entity.Transaction;
import antifraud.service.IpService;
import antifraud.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;

    @Autowired
    private final IpService ipService;

    @Autowired
    private final AppUserService userService;


    public TransactionController(TransactionService transactionService, IpService ipService, AppUserService userService) {
        this.transactionService = transactionService;
        this.ipService = ipService;
        this.userService = userService;
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public @ResponseBody ResponseEntity<Object> addSuspiciousIp(@RequestBody Ip ip) {
        try {
            if (ipService.validateIp(ip.getIp())) {
                ipService.saveIp(ip);
                Ip ipWithId = ipService.findIpByIp(ip.getIp());
                return new ResponseEntity<>(ipWithId, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (NumberFormatException | ChangeSetPersister.NotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }


    @PostMapping("/api/antifraud/transaction")
    public @ResponseBody ResponseEntity<Object> requestTransaction(@Valid @RequestBody Transaction transaction) {
        return new ResponseEntity<>(transactionService.getMoney(transaction), HttpStatus.OK);
    }


}
