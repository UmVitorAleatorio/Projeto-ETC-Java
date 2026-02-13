package controller;

import domain.Review.Avaliation;
import domain.address.Address;
import domain.address.State;
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

    public void menu(int op) {
        switch (op) {
            case 1 -> findByName();
            case 2 -> delete();
            case 3 -> save();
            case 4 -> update();
            case 5 -> createUserFromPerson();
        }
    }

    private void findByName() {
        System.out.println("Digite o nome ou deixe vazio para buscar todos");
        String name = SCANNER.nextLine();
        personRepository.findByName(name)
                .forEach(p -> {
                    if (p.userId() != null) {
                        System.out.printf(
                                "ID [%d] | UID [%d] - %s, Endereço: %s, %s - %s, Tipo: %s, Nota: %.2f, Qtd Notas: %d%n",
                                p.personId(),
                                p.userId(),
                                p.name(),
                                p.street(),
                                p.city(),
                                p.state(),
                                p.documentType(),
                                p.avgRating(),
                                p.ratingCount()
                        );
                    } else {
                        System.out.printf(
                                "ID [%d] - %s, Endereço: %s, %s - %s, Tipo: %s, Nota: %.2f, Qtd Notas: %d%n",
                                p.personId(),
                                p.name(),
                                p.street(),
                                p.city(),
                                p.state(),
                                p.documentType(),
                                p.avgRating(),
                                p.ratingCount()
                        );
                    }
                });
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
        int codeUf = menuSetUf();
        if (codeUf <= 0 || codeUf > 27) {
            System.out.println("O valor escolhido não existe");
            return;
        }
        System.out.println("Escreva o nome da cidade da pessoa (Ex: São Paulo)");
        String city = SCANNER.nextLine();
        System.out.println("Escreva o nome da rua da pessoa (Ex: Av. Paulista)");
        String street = SCANNER.nextLine();
        System.out.println("Digite o número da residência (Ex: 1578)");
        String houseNumber = SCANNER.nextLine();
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

        State state = State.fromCode(codeUf);

        Address address = Address.builder()
                .state(state)
                .city(city)
                .street(street)
                .number(houseNumber)
                .build();

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

        System.out.println("Deseja criar um usuário para essa pessoa? (S/N)");
        String choice = SCANNER.nextLine();

        if ("s".equalsIgnoreCase(choice)) {
            System.out.println("Escreva o email:");
            String email = SCANNER.nextLine();

            System.out.println("Digite a senha:");
            String password = SCANNER.nextLine();

            personService.createPersonWithUser(person, email, password);
            System.out.println("Pessoa e usuário criados com sucesso!");
        } else {
            personService.create(person);
            System.out.println("Pessoa criada com sucesso!");
        }
    }

    private int menuSetUf() {
        System.out.println("Escolha o número correspondente a UF do estado da pessoa");
        System.out.println("1 - AC");
        System.out.println("2 - AL");
        System.out.println("3 - AP");
        System.out.println("4 - AM");
        System.out.println("5 - BA");
        System.out.println("6 - CE");
        System.out.println("7 - DF");
        System.out.println("8 - ES");
        System.out.println("9 - GO");
        System.out.println("10 - MA");
        System.out.println("11 - MT");
        System.out.println("12 - MS");
        System.out.println("13 - MG");
        System.out.println("14 - PA");
        System.out.println("15 - PB");
        System.out.println("16 - PR");
        System.out.println("17 - PE");
        System.out.println("18 - PI");
        System.out.println("19 - RJ");
        System.out.println("20 - RN");
        System.out.println("21 - RS");
        System.out.println("22 - RO");
        System.out.println("23 - RR");
        System.out.println("24 - SC");
        System.out.println("25 - SP");
        System.out.println("26 - SE");
        System.out.println("27 - TO");
        return Integer.parseInt(SCANNER.nextLine());
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

        System.out.println("Deseja alterar o Estado(UF) de residência? S/N");
        String choice = SCANNER.nextLine();

        State state = personFromDB.getAddress().getState();

        if ("s".equalsIgnoreCase(choice)) {
            int codeUf = menuSetUf();
            int oldCodeUf = personFromDB.getAddress().getState().getCode();

            state = State.fromCode(codeUf);

            if (codeUf != oldCodeUf) {
                if (codeUf <= 0 || codeUf > 27) {
                    System.out.println("O valor escolhido não existe");
                    return;
                }
            }
        }

        System.out.println("Escreva o novo nome da cidade ou aperte ENTER para o mesmo");
        String city = SCANNER.nextLine();
        city = city.isEmpty() ? personFromDB.getAddress().getCity() : city;

        System.out.println("Escreva o novo nome da rua ou aperte ENTER para o mesmo");
        String street = SCANNER.nextLine();
        street = street.isEmpty() ? personFromDB.getAddress().getStreet() : street;

        System.out.println("Digite o novo número da residência ou aperte ENTER para o mesmo");
        String numberHouse = SCANNER.nextLine();
        numberHouse = numberHouse.isEmpty() ? personFromDB.getAddress().getNumber() : numberHouse;

        Address address = Address.builder()
                .state(state)
                .city(city)
                .street(street)
                .number(numberHouse)
                .build();

        System.out.println("Deseja alterar o documento (tipo e valor)? S/N");
        choice = SCANNER.nextLine();

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

    private void createUserFromPerson() {
        System.out.println("Digite o ID da pessoa:");
        Integer personId = Integer.parseInt(SCANNER.nextLine());

        Optional<Person> optionalPerson = personRepository.findById(personId);
        if (optionalPerson.isEmpty()) {
            System.out.println("Pessoa não encontrada");
            return;
        }

        if (personService.isUser(personId)) {
            System.out.println("Essa pessoa já é um usuário");
            return;
        }

        System.out.println("Escreva o email:");
        String email = SCANNER.nextLine();

        System.out.println("Digite a senha:");
        String password = SCANNER.nextLine();

        personService.createUserForExistingPerson(personId, email, password);
        System.out.println("Usuário criado com sucesso!");
    }
}
