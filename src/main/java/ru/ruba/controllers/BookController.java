package ru.ruba.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ruba.models.Book;
import ru.ruba.models.Person;
import ru.ruba.services.BookService;
import ru.ruba.services.PeopleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/book")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    private final PeopleService peopleService;


    @Autowired
    public BookController(BookService bookService, PeopleService peopleService) {
        this.bookService = bookService;
        this.peopleService = peopleService;
    }

    /**
     * Обработчик GET-запроса для отображения списка книг с пагинацией и сортировкой.
     *
     * @param model        Модель Spring, используемая для передачи данных в представление.
     * @param page         Номер страницы (необязательный параметр) для пагинации списка книг.
     * @param booksPerPage Количество книг на странице (необязательный параметр) для пагинации списка книг.
     * @param sortByYear   Флаг для указания сортировки списка книг по году (true - сортировать, false - не сортировать).
     * @return Имя представления для отображения списка книг.
     */
    @GetMapping()
    public String index(Model model,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {
        logger.info("Метод index() вызван с параметрами: page = {}, booksPerPage = {}, sortByYear = {}", page, booksPerPage, sortByYear);
        if(page==null || booksPerPage==null) {
            model.addAttribute("books", bookService.findAllBooks(sortByYear));
        }
        else {
            model.addAttribute("books", bookService.findWithPagination(page, booksPerPage, sortByYear));
        }
        return "book/index";
    }

    /**
     * Обработчик GET-запроса для отображения информации о книге с заданным идентификатором.
     *
     * @param id    Идентификатор книги, информацию о которой необходимо отобразить.
     * @param model Модель Spring, используемая для передачи данных в представление.
     * @param person Объект Person, связанный с книгой (необязательный параметр).
     * @return Имя представления для отображения информации о книге.
     */
    @GetMapping("/{id}")
    public String show(@PathVariable ("id") int id, Model model,
                       @ModelAttribute("person") Person person) {
        logger.info("Метод show() вызван с параметром id = {}", id);
        model.addAttribute("book", bookService.findOneBook(id));

        Person bookReader = bookService.getBookReader(id);

        if(bookReader != null)
            model.addAttribute("reader", bookReader);
        else
            model.addAttribute("people", peopleService.findAllPeople());

        return "book/show";
    }

    /**
     * Обработчик GET-запроса для отображения формы создания новой книги.
     *
     * @param book Объект Book, используемый для связывания данных из формы.
     * @return Имя представления для отображения формы создания новой книги.
     */
    @GetMapping("/new")
    public String newBook(@ModelAttribute("books") Book book) {
        logger.info("Метод newBook() вызван");
        return "book/new";
    }

    /**
     * Обработчик POST-запроса для создания новой книги.
     *
     * @param book          Объект Book, содержащий данные новой книги.
     * @param bindingResult Объект BindingResult для проверки ошибок валидации данных.
     * @return Строка перенаправления на страницу со списком книг или страницу создания новой книги в случае ошибок валидации.
     */
    @PostMapping
    public String create(@ModelAttribute("books") @Valid Book book,
                         BindingResult bindingResult) {
        logger.info("Метод create() вызван с объектом Book: {}", book);
        if (bindingResult.hasErrors())
            return "book/new";

        bookService.saveBook(book);
        return "redirect:/book";
    }

    /**
     * Обработчик GET-запроса для открытия страницы редактирования книги.
     *
     * @param model Модель для передачи данных в представление.
     * @param id    Идентификатор книги, которую необходимо отредактировать.
     * @return Страница редактирования книги.
     */
    @GetMapping({"/{id}/edit"})
    public String edit(Model model, @PathVariable("id") int id) {
        logger.info("Метод edit() вызван с параметром id = {}", id);
        model.addAttribute("books", bookService.findOneBook(id));

        return "book/edit";
    }

    /**
     * Обработчик PATCH-запроса для обновления информации о книге.
     *
     * @param book          Обновленная информация о книге.
     * @param bindingResult Результаты валидации данных книги.
     * @param id            Идентификатор книги, которую необходимо обновить.
     * @return Перенаправление на страницу списка книг после успешного обновления или страницу редактирования в случае ошибок.
     */
    @PatchMapping("/{id}")
    public String update(
            @ModelAttribute("books") @Valid Book book,
            BindingResult bindingResult, @PathVariable("id") int id) {
        logger.info("Метод update() вызван с параметрами: id = {}, book = {}", id, book);
        if (bindingResult.hasErrors())
            return "book/edit";

        bookService.updateBook(id, book);
        return "redirect:/book";
    }

    /**
     * Обработчик DELETE-запроса для удаления книги.
     *
     * @param id Идентификатор книги, которую необходимо удалить.
     * @return Перенаправление на страницу списка книг после успешного удаления.
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable ("id") int id){
        logger.info("Метод delete() вызван с параметром id = {}", id);
        bookService.deleteBook(id);
        return "redirect:/book";
    }

    /**
     * Обработчик PATCH-запроса для освобождения книги от читателя.
     *
     * @param id Идентификатор книги, которую необходимо освободить.
     * @return Перенаправление на страницу книги после успешного освобождения.
     */
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        logger.info("Метод release() вызван с параметром id = {}", id);
        bookService.release(id);
        return "redirect:/book/" + id;
    }

    /**
     * Обработчик PATCH-запроса для назначения книги конкретному читателю.
     *
     * @param id             Идентификатор книги, которую необходимо назначить читателю.
     * @param selectedPerson Объект Person, представляющий выбранного читателя для назначения книги.
     *                       Может содержать только поле id, остальные поля - null.
     * @return Перенаправление на страницу книги после успешного назначения.
     */
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        logger.info("Метод assign() вызван с параметрами: id = {}, selectedPerson = {}", id, selectedPerson);
        // у selectedPerson назначено только поле id, остальные поля - null
        bookService.assign(id, selectedPerson);
        return "redirect:/book/" + id;
    }

    /**
     * Отображает страницу поиска книги.
     *
     * @return Название представления (view) для страницы поиска книги.
     */
    @GetMapping("/search")
    public String searchPage() {
        logger.info("Метод searchPage() вызван");
        return "book/search";
    }

    /**
     * Выполняет поиск книги по указанному запросу и отображает результаты поиска на странице поиска книги.
     *
     * @param model Модель, используемая для передачи данных в представление (view).
     * @param query Строка запроса для поиска книги.
     * @return Название представления (view) для страницы поиска книги с результатами.
     */
    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query) {
        logger.info("Метод makeSearch() вызван с параметром query = {}", query);
        model.addAttribute("books", bookService.searchByTitle(query));
        return "book/search";
    }
}
