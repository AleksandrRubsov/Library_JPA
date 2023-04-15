package ru.ruba.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ruba.models.Book;
import ru.ruba.models.Person;
import ru.ruba.repositories.BookRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAllBooks(boolean sortByYear) {
        if(sortByYear)
            return bookRepository.findAll(Sort.by("year"));
        else
            return bookRepository.findAll();
    }

    @PersistenceContext
    private EntityManager entityManager;

    public void clearJpaMetadataCache() {
        entityManager.getEntityManagerFactory().getCache().evictAll();
    }

    public List<Book> findWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if(sortByYear) {
            return bookRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        }
        else {
            return bookRepository.findAll(PageRequest.of(page,booksPerPage)).getContent();
        }
    }
    public Book findOneBook(int id) {
        Optional<Book> foundBook = bookRepository.findById(id);
        return foundBook.orElse(null);
    }

    public List<Book> searchByTitle(String query) {
        return bookRepository.findByTitleStartingWith(query);
    }

    @Transactional
    public void saveBook(Book book) {
        bookRepository.save(book);
    }
    @Transactional
    public void updateBook(int id, Book updatedBook) {
        Book bookToBeUpdated = bookRepository.findById(id).get();
        updatedBook.setId(id);
        updatedBook.setReader(bookToBeUpdated.getReader());

        bookRepository.save(updatedBook);
    }

    @Transactional
    public void deleteBook(int id) {
        bookRepository.deleteById(id);
    }

    public Person getBookReader(int id) {
        return bookRepository.findById(id).map(Book::getReader).orElse(null);
    }

    //освобождаем книгу
    @Transactional
    public void release(int id) {
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setReader(null);
                    book.setTakenAt(null);
                });
    }
    //назначает книгу
    @Transactional
    public void assign(int id, Person selectedPerson) {
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setReader(selectedPerson);
                    book.setTakenAt(new Date());
                }
        );
    }
}
