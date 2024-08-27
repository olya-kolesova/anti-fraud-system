package antifraud.repository;

import antifraud.entity.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {

    Optional<StolenCard> findStolenCardByNumber(String number);

    void deleteStolenCardByNumber(String number);

    List<StolenCard> findAllByOrderById();
}
