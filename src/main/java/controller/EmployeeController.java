package controller;

import lombok.extern.log4j.Log4j2;
import repository.EmployeeRepository;
import service.EmployeeService;
import service.ProfessionService;

import java.util.Scanner;

@Log4j2
public class EmployeeController {
    private static final Scanner SCANNER = new Scanner(System.in);

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final ProfessionService professionService;

    public EmployeeController(EmployeeService employeeService, EmployeeRepository employeeRepository, ProfessionService professionService) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.professionService = professionService;
    }

    public void menu(int op) {
        switch (op) {
            case 1 -> findByName();
            case 2 -> findByAddress();
            case 3 -> findByCodeAndCityWithState();
            case 4 -> save();
            case 5 -> deactivate();
            case 6 -> activate();
            case 7 -> addProfession();
            case 8 -> removeProfession();
        }
    }

    private void findByName() {
        System.out.println("Digite o nome ou deixe vazio para buscar todos");
        String name = SCANNER.nextLine();
        employeeService.findByName(name)
                .forEach(e -> System.out.printf("ID[%d] - UID [%d]: %s, Tipo: %s, Cidade: %s - %s, Funções: %s, Trabalhando: %b, Criado em: %td/%<tB/%<tY, Ativo: %b%n",
                        e.employeeId(),
                        e.userId(),
                        e.name(),
                        e.documentType(),
                        e.address().city(),
                        e.address().state(),
                        e.professionsCode(),
                        e.working(),
                        e.createdAt(),
                        e.active()
                ));
    }

    private void findByAddress() {
        int codeUf = menuSetUf();
        if (codeUf <= 0 || codeUf > 27) {
            System.out.println("O valor escolhido não existe");
            return;
        }
        System.out.println("Escreva o nome da cidade para busca:");
        String city = SCANNER.nextLine();
        employeeService.findByAddress(codeUf, city)
                .forEach(e -> System.out.printf("ID[%d] - UID [%d]: %s, Tipo: %s, Cidade: %s - %s, Funções: %s, Trabalhando: %b, Criado em: %td/%<tB/%<tY, Ativo: %b%n",
                        e.employeeId(),
                        e.userId(),
                        e.name(),
                        e.documentType(),
                        e.address().city(),
                        e.address().state(),
                        e.professionsCode(),
                        e.working(),
                        e.createdAt(),
                        e.active()
                ));
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

    private void findByCodeAndCityWithState(){
        System.out.println("Digite o ID da profissão para busca");
        System.out.println("Profissões disponíveis:");

        professionService.findAll()
                .forEach(p -> System.out.printf("ID[%d] - %s (%s)%n",
                        p.id(),
                        p.code(),
                        p.name())
                );
        Integer professionId = Integer.parseInt(SCANNER.nextLine());


        int codeUf = menuSetUf();
        if (codeUf <= 0 || codeUf > 27) {
            System.out.println("O valor escolhido não existe");
            return;
        }
        System.out.println("Escreva o nome da cidade para busca:");
        String city = SCANNER.nextLine();
        employeeService.findByTagAndCityById(professionId, codeUf, city)
                .forEach(e -> System.out.printf("ID[%d] - UID [%d]: %s, Tipo: %s, Cidade: %s - %s, Funções: %s, Trabalhando: %b, Criado em: %td/%<tB/%<tY, Ativo: %b%n",
                        e.employeeId(),
                        e.userId(),
                        e.name(),
                        e.documentType(),
                        e.address().city(),
                        e.address().state(),
                        e.professionsCode(),
                        e.working(),
                        e.createdAt(),
                        e.active()
                ));
    }

    private void deactivate() {
        System.out.println("Digite o ID do funcionário que você deseja desativar");
        Integer id = Integer.parseInt(SCANNER.nextLine());
        System.out.println("[AVISO] Essa ação NÃO excluirá permanentemente e ela poderá ser revertida [AVISO]");
        System.out.println("Você tem certeza? S/N");
        String choice = SCANNER.nextLine();
        if ("s".equalsIgnoreCase(choice)) employeeService.deactivating(id);
    }

    private void save() {
        System.out.println("Digite o ID de usuário[UID] que será funcionário");
        Integer id = Integer.parseInt(SCANNER.nextLine());

        try {
            employeeService.register(id);
            System.out.println("Funcionário registrado com sucesso!");
        } catch (RuntimeException e) {
            log.error("Erro ao criar funcionário: ", e);
        }
    }

    private void activate() {
        System.out.println("Digite o ID do funcionário que você deseja reativar");
        Integer id = Integer.parseInt(SCANNER.nextLine());
        System.out.println("Você tem certeza? S/N");
        String choice = SCANNER.nextLine();
        if ("s".equalsIgnoreCase(choice)) employeeService.activating(id);
    }

    private void addProfession() {
        System.out.println("Profissões disponíveis:");

        professionService.findAll()
                .forEach(p -> System.out.printf("ID[%d] - %s (%s)%n",
                        p.id(),
                        p.code(),
                        p.name())
                );

        System.out.println("Digite o ID do funcionário:");
        Integer employeeId = Integer.parseInt(SCANNER.nextLine());
        System.out.println("Digite o ID da profissão:");
        Integer professionId = Integer.parseInt(SCANNER.nextLine());

        try {
            employeeService.addProfession(employeeId, professionId);
            System.out.println("Profissão adicionada com sucesso!");
        } catch (RuntimeException e) {
            log.error("Error while trying to add profession", e);
        }
    }

    private void removeProfession() {
        System.out.println("Digite o ID do funcionário:");
        Integer employeeId = Integer.parseInt(SCANNER.nextLine());

        System.out.println("Profissões alocadas neste funcionário:");
        employeeService.findAllProfessionsFromEmployee(employeeId)
                .forEach(p -> System.out.printf("ID[%d] - %s (%S)%n",
                        p.id(),
                        p.code(),
                        p.name())
                );

        System.out.println("Digite o ID da profissão:");
        Integer professionId = Integer.parseInt(SCANNER.nextLine());

        try {
            employeeService.removeProfession(employeeId, professionId);
            System.out.println("Profissão removida");
        } catch (RuntimeException e) {
            log.error("Error while trying to remove profession", e);
        }
    }
}
