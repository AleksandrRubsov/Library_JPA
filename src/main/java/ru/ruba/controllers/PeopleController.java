package ru.ruba.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ruba.models.Person;
import ru.ruba.services.PeopleService;
import ru.ruba.util.PersonValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private static final Logger logger = LoggerFactory.getLogger(PeopleController.class);
    private final PeopleService peopleService;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator) {
        this.peopleService = peopleService;

        this.personValidator = personValidator;
    }

    /**
     * Обработчик GET-запроса для отображения главной страницы со списком людей.
     *
     * @param model Объект модели, используемый для передачи данных в представление.
     * @return Имя представления (шаблона), которое будет отображаться.
     */
    @GetMapping()
    public String index(Model model) {
        logger.info("Вызван метод index()");
        model.addAttribute("people", peopleService.findAllPeople());

        return "people/index";
    }

    /**
     * Обработчик GET-запроса для отображения информации о конкретном человеке по его идентификатору.
     *
     * @param id    Идентификатор человека.
     * @param model Объект модели, используемый для передачи данных в представление.
     * @return Имя представления (шаблона), которое будет отображаться.
     */
    @GetMapping({"/{id}"})
    public String show(@PathVariable("id") int id, Model model) {
        logger.info("Вызван метод show() с id = {}", id);
        model.addAttribute("person", peopleService.findOnePerson(id));
        model.addAttribute("books", peopleService.getBooksByPersonId(id));

        return "people/show";
    }

    /**
     * Обработчик GET-запроса для создания новой записи о человеке.
     *
     * @param person Объект человека, предварительно переданный в модель.
     * @return Имя представления (шаблона) для создания новой записи о человеке.
     */
    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    /**
     * Обработчик POST-запроса для создания новой записи о человеке.
     *
     * @param person         Объект Person, представляющий данные о новом человеке.
     * @param bindingResult  Результат валидации данных в объекте Person.
     * @return Имя представления или URL-адрес для перенаправления после создания записи.
     */
    @PostMapping
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {
        logger.info("Вызван метод create() с объектом Person: {}", person);
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return "people/new";
        }

        peopleService.savePerson(person);
        return "redirect:/people";
    }

    /**
     * Обработчик GET-запроса для редактирования записи о человеке с указанным идентификатором.
     *
     * @param model Модель для передачи данных в представление.
     * @param id    Идентификатор человека, которого нужно отредактировать.
     * @return Имя представления (шаблона) для редактирования записи о человеке.
     */
    @GetMapping({"/{id}/edit"})
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", peopleService.findOnePerson(id));

        return "people/edit";
    }

    /**
     * Обработчик PATCH-запроса для обновления данных о человеке по его идентификатору.
     *
     * @param person         Объект Person с обновленными данными о человеке.
     * @param bindingResult  Результат валидации данных в объекте Person.
     * @param id             Идентификатор человека, данные которого нужно обновить.
     * @return Имя представления или URL-адрес для перенаправления после обновления данных.
     */
    @PatchMapping("/{id}")
    public String update(
            @ModelAttribute("person") @Valid Person person,
            BindingResult bindingResult, @PathVariable("id") int id) {
        logger.info("Вызван метод update() с id = {} и объектом Person: {}", id, person);
        if (bindingResult.hasErrors())
            return "people/edit";

        peopleService.updatePerson(id, person);
        return "redirect:/people";
    }

    /**
     * Обработчик DELETE-запроса для удаления человека по его идентификатору.
     *
     * @param id Идентификатор человека, которого нужно удалить.
     * @return Имя представления или URL-адрес для перенаправления после удаления человека.
     */
    @DeleteMapping
    public String delete(@PathVariable ("id") int id){
        logger.info("Вызван метод delete() с id = {}", id);
        peopleService.deletePerson(id);
        return "redirect:/people";
    }

}
