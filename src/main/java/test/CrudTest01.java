package test;

import controller.PersonController;
import repository.PersonRepository;
import service.PersonService;

import java.util.Scanner;

public class CrudTest01 {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        PersonRepository repository = new PersonRepository();
        PersonService service = new PersonService(repository);
        PersonController controller = new PersonController(service, repository);

        int op;
        while (true) {
            menu();
            op = Integer.parseInt(SCANNER.nextLine());
            if (op == 0) break;
            switch (op) {
                case 1 -> {
                    personMenu();
                    op = Integer.parseInt(SCANNER.nextLine());
                    controller.Menu(op);
                }
            }
        }
    }

    private static void menu() {
        System.out.println("Escolha o numero da operação que você deseja");
        System.out.println("1. Person");
        System.out.println("0. Exit");
    }

    private static void personMenu() {
        System.out.println("Escolha o numero da operação que você deseja");
        System.out.println("1. Procurar Pessoa");
        System.out.println("2. Apagar Pessoa");
        System.out.println("3. Salvar Pessoa");
        System.out.println("4. Atualizar Pessoa");
        System.out.println("9. Voltar");
    }
}
