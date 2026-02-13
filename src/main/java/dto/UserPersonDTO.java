package dto;

import domain.user.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserPersonDTO(
        Integer id,
        Integer personId,
        String email,
        String name,
        Double ratingAvg,
        Integer ratingCount,
        Set<Role> roles
) {
}
