package antifraud;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    public Optional<AppUser> findAppUserByUsername(String username);
}
