package service;

import domain.Review.Avaliation;
import domain.address.Address;
import domain.address.State;
import domain.document.Document;
import domain.document.TypeDocument;
import domain.person.Person;
import domain.user.User;
import repository.AddressRepository;
import repository.PersonRepository;
import repository.UserRepository;

import java.util.Optional;

public class PersonService {

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public PersonService(PersonRepository personRepository, AddressRepository addressRepository, UserService userService, UserRepository userRepository) {
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public String normalizeAndFormatDocument(String document, TypeDocument type) {
        if (document == null) {
            throw new IllegalArgumentException("Documento não pode ser nulo");
        }

        String digitsOnly = document.replaceAll("\\D", "");

        int expectedLength = switch (type){
            case CPF -> 11;
            case CNPJ -> 14;
        };

        if (digitsOnly.length() != expectedLength) {
            throw new IllegalArgumentException(
                    type + "deve conter " + expectedLength + " dígitos"
            );
        }

        return applyMask(digitsOnly, type);
    }

    private String applyMask(String digits, TypeDocument type) {
        return switch (type){
            case CPF -> digits.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
            case CNPJ -> digits.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        };
    }

    private void validateDocumentNotExisting(String newValue, TypeDocument newType, Integer currentIdOrNull) {
        Optional<Person> personOptional = personRepository.findByDocument(newValue, newType);
        if (personOptional.isPresent()) {
            if (currentIdOrNull != null && personOptional.get().getId().equals(currentIdOrNull)) {
                return;
            }
            throw new IllegalStateException("Documento já cadastrado para outra pessoa!");
        }
    }

    public void create(Person person) {
        String formatted = normalizeAndFormatDocument(
                person.getDocument().getValue(),
                person.getDocument().getTypeDocument()
        );

        validateDocumentNotExisting(
                formatted,
                person.getDocument().getTypeDocument(),
                null
        );

        Document newDocument = Document.builder()
                .typeDocument(person.getDocument().getTypeDocument())
                .value(formatted)
                .build();

        Avaliation avaliation = Avaliation.builder()
                .averageRating(5.0)
                .ratingCount(0)
                .build();

        Integer addressId = addressRepository.save(person.getAddress());
        Address addressToSave = Address.builder()
                .id(addressId)
                .state(person.getAddress().getState())
                .city(person.getAddress().getCity())
                .street(person.getAddress().getStreet())
                .number(person.getAddress().getNumber())
                .build();

        Person personToSave = Person.builder()
                .name(person.getName())
                .address(addressToSave)
                .document(newDocument)
                .avaliation(avaliation)
                .build();

        personRepository.save(personToSave);
    }

    public void update(Person person) {
        if (person.getId() == null) throw new IllegalArgumentException("ID é obrigatório para a atualização");

        Person personFromDb = personRepository.findById(person.getId())
                .orElseThrow(() -> new IllegalStateException("Pessoa não encontrada"));

        String name = person.getName() == null || person.getName().isBlank()
                ? personFromDb.getName()
                : person.getName();

        State newState = person.getAddress().getState() != null
                ? person.getAddress().getState()
                : personFromDb.getAddress().getState();

        String newCity = person.getAddress().getCity() != null
                ? person.getAddress().getCity()
                : personFromDb.getAddress().getCity();

        String newStreet = person.getAddress().getStreet() != null
                ? person.getAddress().getStreet()
                : personFromDb.getAddress().getStreet();

        String newNumberHouse = person.getAddress().getNumber() != null
                ? person.getAddress().getNumber()
                : personFromDb.getAddress().getNumber();

        TypeDocument newType = person.getDocument() != null
                ? person.getDocument().getTypeDocument()
                : personFromDb.getDocument().getTypeDocument();

        String newValueRaw = person.getDocument() != null
                ? person.getDocument().getValue()
                : personFromDb.getDocument().getValue();

        String formattedDocument = normalizeAndFormatDocument(newValueRaw, newType);

        validateDocumentNotExisting(
                formattedDocument,
                newType,
                personFromDb.getId()
        );

        Address address = Address.builder()
                .id(personFromDb.getAddress().getId())
                .state(newState)
                .city(newCity)
                .street(newStreet)
                .number(newNumberHouse)
                .build();

        Document document = Document.builder()
                .typeDocument(newType)
                .value(formattedDocument)
                .build();

        Avaliation avaliation = person.getAvaliation();

        Person personToUpdate = Person.builder()
                .id(personFromDb.getId())
                .name(name)
                .address(address)
                .document(document)
                .avaliation(avaliation)
                .build();

        personRepository.update(personToUpdate);
    }

    public void createPersonWithUser(Person person, String email, String password) {
        Integer personId = createAndReturnId(person);
        userService.createUser(personId, email, password);
    }


    private Integer createAndReturnId(Person person) {
        String formatted = normalizeAndFormatDocument(
                person.getDocument().getValue(),
                person.getDocument().getTypeDocument()
        );

        validateDocumentNotExisting(
                formatted,
                person.getDocument().getTypeDocument(),
                null
        );

        Integer addressId = addressRepository.save(person.getAddress());

        Avaliation avaliation = Avaliation.builder()
                .averageRating(5.0)
                .ratingCount(0)
                .build();

        Person personToSave = Person.builder()
                .name(person.getName())
                .address(Address.builder()
                        .id(addressId)
                        .state(person.getAddress().getState())
                        .city(person.getAddress().getCity())
                        .street(person.getAddress().getStreet())
                        .number(person.getAddress().getNumber())
                        .build())
                .document(Document.builder()
                        .typeDocument(person.getDocument().getTypeDocument())
                        .value(formatted)
                        .build())
                .avaliation(avaliation)
                .build();

        return personRepository.save(personToSave);
    }


    public void createUserForExistingPerson(
            Integer personId,
            String email,
            String password
    ) {
        userService.createUser(personId, email, password);
    }

    public boolean isUser(Integer personId) {
        return userRepository.existsByPersonId(personId);
    }
}
