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

@Controller
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    private final PeopleService peopleService;


    @Autowired
    public BookController(BookService bookService, PeopleService peopleService) {
        this.bookService = bookService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {
        if(page==null || booksPerPage==null) {
            model.addAttribute("books", bookService.findAllBooks(sortByYear));
        }
        else {
            model.addAttribute("books", bookService.findWithPagination(page, booksPerPage, sortByYear));
        }
        return "book/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable ("id") int id, Model model,
                       @ModelAttribute("person") Person person) {
        model.addAttribute("book", bookService.findOneBook(id));

        Person bookReader = bookService.getBookReader(id);

        if(bookReader != null)
            model.addAttribute("reader", bookReader);
        else
            model.addAttribute("people", peopleService.findAllPeople());

        return "book/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("books") Book book) {
        return "book/new";
    }

    @PostMapping
    public String create(@ModelAttribute("books") @Valid Book book,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "book/new";

        bookService.saveBook(book);
        return "redirect:/book";
    }

    @GetMapping({"/{id}/edit"})
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("books", bookService.findOneBook(id));

        return "book/edit";
    }

    @PatchMapping("/{id}")
    public String update(
            @ModelAttribute("books") @Valid Book book,
            BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "book/edit";

        bookService.updateBook(id, book);
        return "redirect:/book";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable ("id") int id){
        bookService.deleteBook(id);
        return "redirect:/book";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        bookService.release(id);
        return "redirect:/book/" + id;
    }

    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        // у selectedPerson назначено только поле id, остальные поля - null
        bookService.assign(id, selectedPerson);
        return "redirect:/book/" + id;
    }

    @GetMapping("/search")
    public String searchPage() {
        return "book/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query) {
        model.addAttribute("books", bookService.searchByTitle(query));
        return "book/search";
    }
}
