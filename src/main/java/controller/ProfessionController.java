package controller;

import lombok.extern.log4j.Log4j2;
import service.ProfessionService;

import java.util.Scanner;

@Log4j2
public class ProfessionController {
    private static final Scanner sc = new Scanner(System.in);
    private final ProfessionService professionService;

    public ProfessionController(ProfessionService professionService) {
        this.professionService = professionService;
    }

    public void menu(int op) {
        switch (op) {
            case 1 -> save();
        }
    }

    private void save() {
        int op = optMenuProfessionCode();
        String code = parseOptProfessionCode(op);
        String name = parseOptProfessionName(op);
        if (code != null && name != null) {
            try {
                var profession = professionService.create(code, name);
                System.out.printf("Profissão criada com ID: %d%n", profession.getId());
            } catch (RuntimeException e) {
                log.error("Erro ao criar profissão", e);
            }
        }
    }

    private int optMenuProfessionCode() {
        System.out.println("""
                1- Alvenaria (Pedreiro)
                2- Elétrica (Eletricista)
                3- Hidráulica (Encanador)
                4- Pintura (Pintor)
                5- Mecânica (Mecânico)
                6- Mudança (Carreto)
                9- Sair
                """);
        return Integer.parseInt(sc.nextLine());
    }

    private String parseOptProfessionCode(int op) {
        switch (op) {
            case 1 -> {
                return "MASON";
            }
            case 2 -> {
                return "ELECT";
            }
            case 3 -> {
                return "PLUMB";
            }
            case 4 -> {
                return "PAINT";
            }
            case 5 -> {
                return "MECHA";
            }
            case 6 -> {
                return "MOVER";
            }
        }
        return null;
    }

    private String parseOptProfessionName(int op) {
        switch (op) {
            case 1 -> {
                return "Mason";
            }
            case 2 -> {
                return "Electrician";
            }
            case 3 -> {
                return "Plumber";
            }
            case 4 -> {
                return "Painter";
            }
            case 5 -> {
                return "Mechanic";
            }
            case 6 -> {
                return "Mover";
            }
        }
        return null;
    }
}
