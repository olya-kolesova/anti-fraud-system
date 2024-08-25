package antifraud.service;

import antifraud.entity.Ip;
import antifraud.repository.IpRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class IpService {
    private final IpRepository repository;

    public IpService(IpRepository repository) {
        this.repository = repository;
    }

    public boolean validateIp(String ip) throws NumberFormatException {
        String[] groups = ip.split("\\.");
        if (groups.length != 4) {
            return false;
        }
        return Arrays.stream(groups).map(Integer::parseInt).allMatch(x -> x <= 255 && x > 0);
    }

    public void saveIp(Ip ip) {
        repository.save(ip);
    }

    public Ip findIpByIp(String ip) throws ChangeSetPersister.NotFoundException {
        return repository.findByIp(ip).orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    public boolean isIpPresent(String ip) {
        return repository.findByIp(ip).isPresent();
    }
    @Transactional
    public void deleteByIp(String ip) throws ChangeSetPersister.NotFoundException {
        if (isIpPresent(ip)) {
            repository.deleteByIp(ip);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }

    }

    public List<Ip> getAllIpSorted() {
        return repository.findAllByOrderById();
    }

}
