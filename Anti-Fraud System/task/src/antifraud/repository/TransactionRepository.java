package antifraud.repository;
import antifraud.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT t FROM Transaction t WHERE t.date BETWEEN ?1 AND ?2")
    List<Transaction> getAllTransactionsByAmountIpNumberWithinHour(LocalDateTime dateFrom, LocalDateTime dateTo);

}
