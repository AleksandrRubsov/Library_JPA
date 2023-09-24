package ru.ruba.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ruba.models.Person;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    /**
     * Находит человека в репозитории по указанному ФИО (Фамилия, Имя, Отчество).
     *
     * @param fio ФИО (Фамилия, Имя, Отчество) человека, которого нужно найти.
     * @return Optional, содержащий найденного человека, если человек с указанным ФИО существует, иначе пустой Optional.
     */
    Optional<Person> findByFio(String fio);
}
