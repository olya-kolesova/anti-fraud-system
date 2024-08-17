package antifraud;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByUsername(String username);
    List<AppUser> findByOrderById();

    void deleteByUsername(String username);

}
