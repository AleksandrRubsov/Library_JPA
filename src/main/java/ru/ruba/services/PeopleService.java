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

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAllPeople() {
        return peopleRepository.findAll();
    }

    public Person findOnePerson(int id) {
        Optional<Person> foundPerson =  peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    @Transactional
    public void savePerson(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void updatePerson(int id, Person updatePerson) {
        updatePerson.setId(id);
        peopleRepository.save(updatePerson);
    }
    @Transactional
    public void deletePerson(int id) {
        peopleRepository.deleteById(id);
    }

    public Optional<Person> getPersonByFio(String fio) {
        return peopleRepository.findByFio(fio);
    }

    public List<Book> getBooksByPersonId(int id) {
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
