package dto;

import domain.profession.Profession;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record EmployeePersonDTO(
        Integer employeeId,
        Integer personId,
        Integer userId,
        String name,
        String documentValue,
        String documentType,
        boolean working,
        boolean active,
        LocalDateTime createdAt,
        AddressDTO address,
        List<ProfessionDTO> professions
) {
    public EmployeePersonDTO {
        if (professions == null) {
            professions = new ArrayList<>();
        }
    }

    public String professionsCode(){
        if (professions == null || professions.isEmpty()) {
            return "Nenhuma";
        }

        return professions.stream()
                .map(ProfessionDTO::code)
                .collect(Collectors.joining(", "));
    }
}