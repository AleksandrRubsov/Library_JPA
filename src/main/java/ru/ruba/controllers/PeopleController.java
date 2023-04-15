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

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator) {
        this.peopleService = peopleService;

        this.personValidator = personValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAllPeople());

        return "people/index";
    }

    @GetMapping({"/{id}"})
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.findOnePerson(id));
        model.addAttribute("books", peopleService.getBooksByPersonId(id));

        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/new";
        }

        peopleService.savePerson(person);
        return "redirect:/people";
    }

    @GetMapping({"/{id}/edit"})
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", peopleService.findOnePerson(id));

        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(
            @ModelAttribute("person") @Valid Person person,
            BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "people/edit";

        peopleService.updatePerson(id, person);
        return "redirect:/people";
    }

    @DeleteMapping
    public String delete(@PathVariable ("id") int id){
        peopleService.deletePerson(id);
        return "redirect:/people";
    }

}
