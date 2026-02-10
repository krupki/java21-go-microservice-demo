package de.krupki.persons.controller;

import de.krupki.persons.dto.PersonCreateDto;
import de.krupki.persons.dto.PersonResponseDto;
import de.krupki.persons.service.PersonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personen")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public PersonResponseDto create(@RequestBody PersonCreateDto createDto) {
        return personService.createPerson(createDto);
    }

    @GetMapping
    public List<PersonResponseDto> getAll() {
        return personService.getAllPersons();
    }

    @GetMapping("/sorted")
    public List<PersonResponseDto> getAllSorted(){
        return personService.getAllPersonsSorted();
    }
}
