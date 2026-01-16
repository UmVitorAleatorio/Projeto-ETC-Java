package dto;

import domain.user.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserPersonDTO(
        Integer id,
        String email,
        String name,
        String address,
        Double ratingAvg,
        int ratingCount,
        Set<Role> roles
) {
}
