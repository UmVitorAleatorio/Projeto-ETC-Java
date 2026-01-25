package controller;

import domain.Review.Avaliation;
import domain.document.Document;
import domain.document.TypeDocument;
import domain.person.Person;
import repository.PersonRepository;
import service.PersonService;

import java.util.Optional;
import java.util.Scanner;

public class PersonController {
    private static final Scanner SCANNER = new Scanner(System.in);

    private final PersonService personService;
    private final PersonRepository personRepository;

    public PersonController(PersonService personService, PersonRepository personRepository) {
        this.personService = personService;
        this.personRepository = personRepository;
    }

    public void Menu(int op) {
        switch (op) {
            case 1 -> findByName();
            case 2 -> delete();
            case 3 -> save();
            case 4 -> update();
        }
    }

    private void findByName() {
        System.out.println("Digite o nome ou deixe vazio para buscar todos");
        String name = SCANNER.nextLine();
        personRepository.findByName(name)
                .forEach(p -> System.out.printf("[%d] - %s, Endereço: %s, Tipo: %s, Nota: %.2f, Quantidade de Notas: %d%n",
                        p.getId(),
                        p.getName(),
                        p.getAddress(),
                        p.getDocument().getTypeDocument(),
                        p.getAvaliation().getAverageRating(),
                        p.getAvaliation().getRatingCount()
                ));
    }

    private void delete() {
        System.out.println("Digite o ID da pessoa que você deseja excluir");
        int id = Integer.parseInt(SCANNER.nextLine());
        System.out.println("Você tem certeza? S/N");
        String choice = SCANNER.nextLine();
        if ("s".equalsIgnoreCase(choice)) personRepository.delete(id);
    }

    private void save() {
        System.out.println("Escreva o nome da pessoa");
        String name = SCANNER.nextLine();
        System.out.println("Escreva o endereço da pessoa");
        String address = SCANNER.nextLine();
        System.out.println("Digite o numero correspondente ao tipo do documento: 1 - CPF, 2 - CNPJ");
        int opDocType = Integer.parseInt(SCANNER.nextLine());

        if (opDocType != 1 && opDocType != 2) {
            System.out.println("O valor escolhido não existe");
            return;
        }
        TypeDocument typeDocument = TypeDocument.fromCode(opDocType);

        if (opDocType == 1) {
            System.out.println("Digite o valor do seu CPF");
        } else {
            System.out.println("Digite o valor do seu CNPJ");
        }
        String documentValue = SCANNER.nextLine();

        Document document = Document.builder()
                .typeDocument(typeDocument)
                .value(documentValue)
                .build();

        double averageRating = Avaliation.builder().build().getAverageRating();
        int ratingCount = Avaliation.builder().build().getRatingCount();

        Avaliation avaliation = Avaliation.builder()
                .averageRating(averageRating)
                .ratingCount(ratingCount)
                .build();

        Person person = Person.builder()
                .name(name)
                .address(address)
                .document(document)
                .avaliation(avaliation)
                .build();
        personService.create(person);
    }

    private void update() {
        System.out.println("Digite o ID da pessoa para a atualização");
        Optional<Person> personOptional = personRepository.findById(Integer.parseInt(SCANNER.nextLine()));
        if (personOptional.isEmpty()) {
            System.out.println("Pessoa não encontrada");
            return;
        }
        Person personFromDB = personOptional.get();
        System.out.println("Pessoa encontrada: " + personFromDB);
        System.out.println("Escreva o novo nome da pessoa ou aperte ENTER para o mesmo");
        String name = SCANNER.nextLine();
        name = name.isEmpty() ? personFromDB.getName() : name;

        System.out.println("Escreva o novo nome do endereço ou aperte ENTER para o mesmo");
        String address = SCANNER.nextLine();
        address = address.isEmpty() ? personFromDB.getAddress() : address;

        System.out.println("Deseja alterar o documento (tipo e valor)? S/N");
        String choice = SCANNER.nextLine();

        TypeDocument typeDocument = personFromDB.getDocument().getTypeDocument();
        String documentValue = personFromDB.getDocument().getValue();

        if ("s".equalsIgnoreCase(choice)) {
            System.out.println("Digite o numero correspondente ao tipo do documento: 1 - CPF, 2 - CNPJ");
            int opToNewDocType = Integer.parseInt(SCANNER.nextLine());
            int oldDocType = personFromDB.getDocument().getTypeDocument().getCode();

            typeDocument = TypeDocument.fromCode(opToNewDocType);

            if (opToNewDocType != oldDocType) {
                if (opToNewDocType != 1 && opToNewDocType != 2) {
                    System.out.println("O valor escolhido não existe");
                    return;
                }
                if (opToNewDocType == 1) {
                    System.out.println("Digite o valor do seu CPF");
                } else {
                    System.out.println("Digite o valor do seu CNPJ");
                }
                documentValue = SCANNER.nextLine();

            } else {
                documentValue = personFromDB.getDocument().getValue();
            }
        }

        Document document = Document.builder()
                .typeDocument(typeDocument)
                .value(documentValue)
                .build();

        Person personToUpdate = Person.builder()
                .id(personFromDB.getId())
                .name(name)
                .address(address)
                .document(document)
                .avaliation(personFromDB.getAvaliation())
                .build();

        personService.update(personToUpdate);
    }
}
