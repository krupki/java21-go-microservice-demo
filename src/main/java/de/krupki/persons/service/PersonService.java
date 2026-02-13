package de.krupki.persons.service;

import de.krupki.persons.dto.PersonCreateDto;
import de.krupki.persons.dto.PersonResponseDto;
import de.krupki.persons.entity.Person;
import de.krupki.persons.repository.PersonRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import de.krupki.persons.PersonSorterGrpc;
import de.krupki.persons.SortRequest;
import de.krupki.persons.SortResponse;
import de.krupki.persons.PersonMsg;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class PersonService {

    private final PersonRepository repository;
    private final PersonSorterGrpc.PersonSorterBlockingStub sortingStub;

    public PersonService(PersonRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        this.sortingStub = PersonSorterGrpc.newBlockingStub(channel);
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

            // convert db info into grpc-message
            List<PersonMsg> protoPersons = repository.findAll().stream()
                    .map(p -> PersonMsg.newBuilder()
                            .setId(p.getId())
                            .setName(p.getName())
                            .setAge(p.getAge())
                            .build())
                        .toList();

            // build request
            SortRequest request = SortRequest.newBuilder()
                    .addAllPersons(protoPersons)
                    .build();

            // init go service
            SortResponse response = sortingStub.sort(request);

            // map response to dto
                   return response.getPersonsList().stream()
                           .map(p -> new PersonResponseDto(p.getId(), p.getName(), p.getAge()))
                           .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
