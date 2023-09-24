package ru.ruba.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ruba.models.Book;
import ru.ruba.models.Person;
import ru.ruba.repositories.PeopleRepository;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private static final Logger logger = LoggerFactory.getLogger(PeopleService.class);
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    /**
     * Получает список всех людей.
     *
     * @return Список всех людей в базе данных.
     */
    public List<Person> findAllPeople() {
        logger.info("Вызван метод findAllPeople()");
        return peopleRepository.findAll();
    }

    /**
     * Находит человека по указанному идентификатору.
     *
     * @param id Идентификатор человека, которого нужно найти.
     * @return Найденный человек или null, если человек не был найден.
     */
    public Person findOnePerson(int id) {
        logger.info("Вызван метод findOnePerson() с id = {}", id);
        Optional<Person> foundPerson =  peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    /**
     * Сохраняет информацию о человеке в хранилище данных.
     *
     * @param person Объект Person, который должен быть сохранен.
     */
    @Transactional
    public void savePerson(Person person) {
        logger.info("Вызван метод savePerson() с объектом Person: {}", person);
        peopleRepository.save(person);
    }

    /**
     * Обновляет информацию о человеке с указанным идентификатором.
     *
     * @param id          Идентификатор человека, информацию о котором необходимо обновить.
     * @param updatePerson Обновленный объект Person с новой информацией.
     */
    @Transactional
    public void updatePerson(int id, Person updatePerson) {
        logger.info("Вызван метод updatePerson() с id = {} и объектом Person: {}", id, updatePerson);
        updatePerson.setId(id);
        peopleRepository.save(updatePerson);
    }

    /**
     * Удаляет человека с указанным идентификатором из хранилища.
     *
     * @param id Идентификатор человека, которого необходимо удалить.
     */
    @Transactional
    public void deletePerson(int id) {
        logger.info("Вызван метод deletePerson() с id = {}", id);
        peopleRepository.deleteById(id);
    }

    /**
     * Ищет человека в хранилище по его полному имени (ФИО).
     *
     * @param fio Полное имя (ФИО) человека, которого нужно найти.
     * @return Объект типа Optional<Person>, содержащий найденного человека, если он существует, или пустой Optional, если человек не найден.
     */
    public Optional<Person> getPersonByFio(String fio) {
        logger.info("Вызван метод getPersonByFio() с параметром fio = {}", fio);
        return peopleRepository.findByFio(fio);
    }

    /**
     * Возвращает список книг, принадлежащих человеку с указанным идентификатором.
     *
     * @param id Идентификатор человека, для которого нужно получить список книг.
     * @return Список книг, принадлежащих человеку, или пустой список, если человек не найден или не имеет книг.
     */
    public List<Book> getBooksByPersonId(int id) {
        logger.info("Вызван метод getBooksByPersonId() с id = {}", id);
        Optional<Person> person = peopleRepository.findById(id);

        if(person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());

            //проверка просроченности книг
            person.get().getBooks().forEach(book -> {
                long diffInMillies = Math.abs(book.getTakenAt().getTime() - new Date().getTime());

                if(diffInMillies > 864000000) {
                    book.setExpired(true); //книга просрочена
                } //10 суток
            });

            return person.get().getBooks();
        }
        else {
            return Collections.emptyList();
        }
    }
}
