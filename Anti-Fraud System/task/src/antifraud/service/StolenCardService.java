package antifraud.service;

import antifraud.entity.StolenCard;
import antifraud.repository.StolenCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class StolenCardService {
    @Autowired
    StolenCardRepository repository;

    public StolenCardService(StolenCardRepository repository) {
        this.repository = repository;
    }

    public boolean checkCard(String number) throws NumberFormatException {
        if (number.length() != 16) {
            return false;
        }

        int checkNum = Character.digit(number.charAt(number.length() - 1), 10);
        List<Integer> list = new StringBuilder(number).reverse().toString().chars()
                .map(x -> Character.digit((char) x, 10)).boxed().toList().subList(1, number.length());

        int limit = list.size() % 2 != 0 ? list.size() / 2 + 1 : list.size() / 2;

        List<Integer> listOfDoubled = IntStream.iterate(0, i -> i + 2).limit(limit).mapToObj(list::get)
                .map(x -> 2 * x).toList();

        int sumOfDigits = IntStream.iterate(1, i -> i + 2).limit(list.size() / 2).mapToObj(list::get)
                .reduce(0, Integer::sum);

        int sumOfTwoDigit = listOfDoubled.stream().map(Object::toString).filter(x -> x.length() > 1)
                .map(x -> Character.digit(x.charAt(0), 10) + Character.digit(x.charAt(1), 10))
                .reduce(0, Integer::sum);

        int sumOfOneDigit = listOfDoubled.stream().filter(x -> x.toString().length() == 1).reduce(0, Integer::sum);

        int summedCheck = 10 - ((sumOfDigits + sumOfOneDigit + sumOfTwoDigit) % 10);

        return summedCheck == checkNum;
    }

    public void saveCard(StolenCard card) {
        repository.save(card);
    }

    public StolenCard findCardByNumber(String number) throws ChangeSetPersister.NotFoundException {
        return repository.findStolenCardByNumber(number).orElseThrow(
            () -> new ChangeSetPersister.NotFoundException());
    }

    public boolean isCardPresent(String number) {
        return repository.findStolenCardByNumber(number).isPresent();
    }
    @Transactional
    public void deleteCard(String number) {
        repository.deleteStolenCardByNumber(number);
    }

    public List<StolenCard> findAllSortedById() {
        return repository.findAllByOrderById();
    }

}
