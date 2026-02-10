package de.krupki.persons.service;

import de.krupki.persons.dto.PersonCreateDto;
import de.krupki.persons.dto.PersonResponseDto;
import de.krupki.persons.entity.Person;
import de.krupki.persons.repository.PersonRepository;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class PersonService {

    private final PersonRepository repository;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PersonService(PersonRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().build();
    }

    public PersonResponseDto createPerson(PersonCreateDto createDto) {
        Person entity = new Person();
        entity.setName(createDto.name());
        entity.setAge(createDto.age());

        Person savedEntity = repository.save(entity);

        return new PersonResponseDto(
                savedEntity.getId(),
                savedEntity.getName(),
                savedEntity.getAge()
        );
    }

    public List<PersonResponseDto> getAllPersons() {
        return repository.findAll()
                .stream()
                .map(PersonResponseDto::fromEntity)
                .toList();
    }

    public List<PersonResponseDto> getAllPersonsSorted() {
        try {
            // objectliste organisieren
            List<PersonResponseDto> persons = repository.findAll().stream().map(PersonResponseDto::fromEntity).toList();
            // objectliste zu json umwandeln
            String jsonPayload = objectMapper.writeValueAsString(persons);
            // Request an Go
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/sort"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // Senden und Antwort empfangen
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // sortiertes JSON wieder in Liste umwandeln
            return objectMapper.readValue(response.body(), new TypeReference<List<PersonResponseDto>>() {
            });
        } catch (Exception e) {
            // Fallback: Falls Go offline ist, gib die unsortierte Liste zurück
            System.err.println("Go-Service Fehler: " + e.getMessage());
            return repository.findAll().stream().map(PersonResponseDto::fromEntity).toList();
        }
    }
}
