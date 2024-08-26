package antifraud.repository;

import antifraud.entity.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {

    Optional<StolenCard> findStolenCardByNumber(String number);
}
