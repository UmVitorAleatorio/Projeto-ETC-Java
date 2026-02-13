package test;

import controller.EmployeeController;
import controller.PersonController;
import controller.ProfessionController;
import repository.*;
import service.EmployeeService;
import service.PersonService;
import service.ProfessionService;
import service.UserService;

import java.util.Scanner;

public class CrudTest01 {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        PersonRepository personRepository = new PersonRepository();
        AddressRepository addressRepo = new AddressRepository();
        UserRepository userRepo = new UserRepository();
        UserService userService = new UserService(userRepo);
        PersonService personService = new PersonService(personRepository, addressRepo, userService, userRepo);
        PersonController personController = new PersonController(personService, personRepository);
        EmployeeRepository employeeRepository = new EmployeeRepository();
        ProfessionRepository professionRepository = new ProfessionRepository();
        EmployeeProfessionRepository employeeProfessionRepository = new EmployeeProfessionRepository();
        EmployeeService employeeService = new EmployeeService(employeeRepository, employeeProfessionRepository, professionRepository);
        ProfessionService professionService = new ProfessionService(professionRepository);
        ProfessionController professionController = new ProfessionController(professionService);
        EmployeeController employeeController = new EmployeeController(employeeService, employeeRepository, professionService);

        int op;
        while (true) {
            menu();
            op = Integer.parseInt(SCANNER.nextLine());
            if (op == 0) break;
            switch (op) {
                case 1 -> {
                    personMenu();
                    op = Integer.parseInt(SCANNER.nextLine());
                    personController.menu(op);
                }
                case 2 -> {
                    employeeMenu();
                    op = Integer.parseInt(SCANNER.nextLine());
                    employeeController.menu(op);
                }
                case 3 -> {
                    professionMenu();
                    op = Integer.parseInt(SCANNER.nextLine());
                    professionController.menu(op);
                }
            }
        }
    }

    private static void menu() {
        System.out.println("Escolha o número da operação que você deseja");
        System.out.println("1. Person");
        System.out.println("2. Employee");
        System.out.println("3. Profession");
        System.out.println("0. Exit");
    }

    private static void personMenu() {
        System.out.println("Escolha o número da operação que você deseja");
        System.out.println("1. Procurar Pessoa");
        System.out.println("2. Apagar Pessoa");
        System.out.println("3. Salvar Pessoa");
        System.out.println("4. Atualizar Pessoa");
        System.out.println("5. Criar Usuário para Pessoa");
        System.out.println("9. Voltar");
    }

    private static void employeeMenu() {
        System.out.println("Escolha o número da operação que você deseja");
        System.out.println("1. Procurar Funcionário");
        System.out.println("2. Procurar Funcionário por endereço");
        System.out.println("3. Procurar Profissional por endereço");
        System.out.println("4. Salvar Usuário como Funcionário");
        System.out.println("5. Desativar Funcionário");
        System.out.println("6. Reativar Funcionário");
        System.out.println("7. Adicionar Profissão a Funcionário");
        System.out.println("8. Remover Profissão de Funcionário");
        System.out.println("9. Voltar");
    }

    private static void professionMenu() {
        System.out.println("Escolha o número da operação que você deseja");
        System.out.println("1. Criar profissão no programa");
        System.out.println("9. Voltar");
    }
}
