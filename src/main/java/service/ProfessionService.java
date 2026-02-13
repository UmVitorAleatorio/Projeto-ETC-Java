package service;

import domain.profession.Profession;
import dto.ProfessionDTO;
import repository.ProfessionRepository;

import java.util.List;

public class ProfessionService {
    private final ProfessionRepository professionRepository;

    public ProfessionService(ProfessionRepository professionRepository) {
        this.professionRepository = professionRepository;
    }

    public Profession create(String code, String name) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code is required");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        Profession profession = Profession.builder()
                .code(code.trim())
                .name(name.trim())
                .build();

        return professionRepository.save(profession);
    }

    public List<ProfessionDTO> findAll() {
        return professionRepository.findAll();
    }
}
