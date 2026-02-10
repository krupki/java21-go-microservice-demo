package de.krupki.persons.dto;

import de.krupki.persons.entity.Person;

public record PersonResponseDto(
        Long id,
        String name,
        int age
) {
    public static PersonResponseDto fromEntity(Person person) {
        return new PersonResponseDto(person.getId(), person.getName(), person.getAge());
    }
}
