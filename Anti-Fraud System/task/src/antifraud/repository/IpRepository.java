package antifraud.repository;

import antifraud.entity.Ip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpRepository extends JpaRepository<Ip, Long> {

    Optional<Ip> findByIp(String ip);

    void deleteByIp(String ip);

    List<Ip> findAllByOrderById();
}
