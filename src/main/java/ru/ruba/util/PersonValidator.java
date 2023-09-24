package ru.ruba.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ruba.models.Person;
import ru.ruba.services.PeopleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PersonValidator implements Validator {
    private static final Logger logger = LoggerFactory.getLogger(PersonValidator.class);

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator( PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    /**
     * Проверяет, поддерживает ли данный валидатор указанный класс объекта.
     *
     * @param clazz Класс объекта для проверки.
     * @return true, если валидатор поддерживает указанный класс объекта, в противном случае - false.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    /**
     * Проверяет, существует ли человек с указанным ФИО, и добавляет сообщение об ошибке, если такой человек уже существует.
     *
     * @param target Объект, подлежащий валидации (в данном случае, объект типа Person).
     * @param errors Объект, в котором хранятся сообщения об ошибках валидации.
     */
    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if (peopleService.getPersonByFio(person.getFio()).isPresent())
            errors.rejectValue("fio", "", "Человек с таким ФИО уже существует");
        logger.warn("Ошибка валидации: Человек с ФИО {} уже существует", person.getFio());
    }
}