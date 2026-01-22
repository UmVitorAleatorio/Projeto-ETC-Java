package service;

import domain.document.TypeDocument;
import domain.person.Person;
import repository.PersonRepository;

import java.util.Optional;

public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
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
}
