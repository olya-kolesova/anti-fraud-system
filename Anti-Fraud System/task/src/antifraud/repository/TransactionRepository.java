package antifraud.repository;
import antifraud.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.number = ?1 AND t.ip = ?2 AND t.date BETWEEN ?3 AND ?4")
    List<Transaction> getTransactionsByNumberAmountIpWithinHour(String number, String ip, LocalDateTime dateFrom,
        LocalDateTime dateTo);


    @Query("SELECT t FROM Transaction t WHERE t.number = ?1 AND t.region = ?2 AND t.date BETWEEN ?3 AND ?4")
    List<Transaction> getTransactionsByNumberAmountRegionWithinHour(String number, String region, LocalDateTime dateFrom,
        LocalDateTime dateTo);


    List<Transaction> findAllByNumberOrderById(String number);
}
