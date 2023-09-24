package ru.ruba.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ruba.models.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    /**
     * Находит список книг, названия которых начинаются с указанной строки.
     *
     * @param title Начальная строка, с которой должны начинаться названия книг.
     * @return Список книг, названия которых начинаются с указанной строки.
     */
    List<Book> findByTitleStartingWith(String title);
}
