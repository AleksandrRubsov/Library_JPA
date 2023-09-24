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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(readOnly = true)
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Получает список всех книг из репозитория
     * @param sortByYear Флаг, указывающий на необходимость сортировки по году выпуска книги.
     *                 Если установлен в true, книги будут отсортированы по году выпуска;
     *                 если установлен в false, книги будут возвращены в порядке, в котором они находятся в репозитории.
     * @return Список объектов Book, представляющих собой все книги из репозитория.
     */
    public List<Book> findAllBooks(boolean sortByYear) {
        logger.info("Метод findAllBooks() вызван с параметром sortByYear = {}", sortByYear);
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

    /**
     * Получает список книг с пагинацией из репозитория.
     *
     * @param page          Номер страницы, которую нужно получить. Нумерация начинается с 0.
     * @param booksPerPage  Количество книг на одной странице.
     * @param sortByYear    Флаг, указывающий на необходимость сортировки книг на странице по году выпуска.
     *                      Если установлен в true, книги на странице будут отсортированы по году выпуска;
     *                      если установлен в false, книги будут возвращены без сортировки.
     * @return Список объектов Book, представляющих собой книги на запрошенной странице с учетом пагинации и сортировки (по году, если указан флаг).
     */
    public List<Book> findWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        logger.info("Метод findWithPagination() вызван с параметрами: page = {}, booksPerPage = {}, sortByYear = {}", page, booksPerPage, sortByYear);
        if(sortByYear) {
            return bookRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        }
        else {
            return bookRepository.findAll(PageRequest.of(page,booksPerPage)).getContent();
        }
    }

    /**
     * Находит книгу по указанному идентификатору в репозитории.
     *
     * @param id Идентификатор книги, которую нужно найти.
     * @return Объект Book, представляющий собой найденную книгу, или null, если книга не найдена.
     */
    public Book findOneBook(int id) {
        logger.info("Метод findOneBook() вызван с параметром id = {}", id);
        Optional<Book> foundBook = bookRepository.findById(id);
        return foundBook.orElse(null);
    }

    /**
     * Выполняет поиск книг по начальной части заголовка.
     *
     * @param query Строка, по которой выполняется поиск начальной части заголовка книги.
     * @return Список книг, заголовок которых начинается с указанной строки `query`.
     */
    public List<Book> searchByTitle(String query) {
        logger.info("Метод searchByTitle() вызван с параметром query = {}", query);
        return bookRepository.findByTitleStartingWith(query);
    }

    /**
     * Сохраняет книгу в репозитории.
     *
     * @param book Объект книги, который нужно сохранить.
     */
    @Transactional
    public void saveBook(Book book) {
        logger.info("Метод saveBook() вызван с объектом Book: {}", book);
        bookRepository.save(book);
    }

    /**
     * Обновляет информацию о книге в репозитории.
     *
     * @param id           Идентификатор книги, которую нужно обновить.
     * @param updatedBook  Обновленная информация о книге.
     */
    @Transactional
    public void updateBook(int id, Book updatedBook) {
        logger.info("Метод updateBook() вызван с параметрами: id = {}, updatedBook = {}", id, updatedBook);
        Book bookToBeUpdated = bookRepository.findById(id).get();
        updatedBook.setId(id);
        updatedBook.setReader(bookToBeUpdated.getReader());

        bookRepository.save(updatedBook);
    }

    @Transactional
    public void deleteBook(int id) {
        logger.info("Метод deleteBook() вызван с параметром id = {}", id);
        bookRepository.deleteById(id);
    }

    public Person getBookReader(int id) {
        logger.info("Метод getBookReader() вызван с параметром id = {}", id);
        return bookRepository.findById(id).map(Book::getReader).orElse(null);
    }

    //освобождаем книгу
    @Transactional
    public void release(int id) {
        logger.info("Метод release() вызван с параметром id = {}", id);
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setReader(null);
                    book.setTakenAt(null);
                });
    }
    //назначает книгу
    @Transactional
    public void assign(int id, Person selectedPerson) {
        logger.info("Метод assign() вызван с параметрами: id = {}, selectedPerson = {}", id, selectedPerson);
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setReader(selectedPerson);
                    book.setTakenAt(new Date());
                }
        );
    }
}
