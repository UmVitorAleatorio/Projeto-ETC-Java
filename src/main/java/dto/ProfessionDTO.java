package dto;

import lombok.Builder;

@Builder
public record ProfessionDTO(
        Integer id,
        String code,
        String name
) {
}
