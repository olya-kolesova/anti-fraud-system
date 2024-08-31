package antifraud.controller;

import antifraud.dto.TransactionDTO;
import antifraud.entity.Ip;
import antifraud.entity.StolenCard;
import antifraud.service.AppUserService;
import antifraud.entity.Transaction;
import antifraud.service.IpService;
import antifraud.service.StolenCardService;
import antifraud.service.TransactionService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;

    @Autowired
    private final IpService ipService;

    @Autowired
    private final StolenCardService stolenCardService;

    @Autowired
    private final AppUserService userService;

    public record TransactionRecord(String result, String info) {}



    public TransactionController(TransactionService transactionService, IpService ipService, StolenCardService stolenCardService, AppUserService userService) {
        this.transactionService = transactionService;
        this.ipService = ipService;
        this.stolenCardService = stolenCardService;
        this.userService = userService;
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public @ResponseBody ResponseEntity<Object> addSuspiciousIp(@RequestBody Ip ip) {
        try {
            if (ipService.validateIp(ip.getIp())) {
                if (ipService.isIpPresent(ip.getIp())) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
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


    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public @ResponseBody ResponseEntity<Object> deleteIp(@PathVariable String ip) {
        try {
            if (ipService.validateIp(ip)) {
                ipService.deleteByIp(ip);
                Map<String, String> reply = new HashMap<>();
                String status = String.format("IP %s successfully removed!", ip);
                reply.put("status", status);
                return new ResponseEntity<>(reply, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (NumberFormatException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ChangeSetPersister.NotFoundException exceptionNotFound) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


    @GetMapping("/api/antifraud/suspicious-ip")
    public @ResponseBody ResponseEntity<Object> getListIp() {
        return new ResponseEntity<>(ipService.getAllIpSorted(), HttpStatus.OK);
    }


    @PostMapping("/api/antifraud/stolencard")
    public @ResponseBody ResponseEntity<Object> addStolenCard(@RequestBody StolenCard card) {
        try {
            if (stolenCardService.checkCard(card.getNumber())) {
                if (stolenCardService.isCardPresent(card.getNumber())) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                stolenCardService.saveCard(card);
                StolenCard savedCard = stolenCardService.findCardByNumber(card.getNumber());
                return new ResponseEntity<>(savedCard, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (NumberFormatException | ChangeSetPersister.NotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public @ResponseBody ResponseEntity<Object> deleteCard(@PathVariable String number) {
        try {
           if (stolenCardService.checkCard(number)) {
               if (stolenCardService.isCardPresent(number)) {
                   stolenCardService.deleteCard(number);
                   Map<String, String> status = new HashMap<>();
                   status.put("status", String.format("Card %s successfully removed!", number));

                   return new ResponseEntity<>(status, HttpStatus.OK);
               } else {
                   return new ResponseEntity<>(HttpStatus.NOT_FOUND);
               }
           } else {
               return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
           }
        } catch (NumberFormatException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/api/antifraud/stolencard")
    public @ResponseBody ResponseEntity<List<StolenCard>> getAllCards() {
        return new ResponseEntity<>(stolenCardService.findAllSortedById(), HttpStatus.OK);
    }


    @PostMapping("/api/antifraud/transaction")
    public @ResponseBody ResponseEntity<Object> requestTransaction(@Valid @RequestBody TransactionDTO transactionDto) {
        try {
            Transaction transaction = transactionService.convertDtoToTransaction(transactionDto);
            Transaction checkedTransaction = transactionService.getMoney(transaction);
            TransactionRecord transactionRecord = new TransactionRecord(checkedTransaction.getResult(), checkedTransaction.getInfo());
            return new ResponseEntity<>(transactionRecord, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
