package dto;

import lombok.Builder;

@Builder
public record PersonWithUserDTO(
        Integer personId,
        String name,
        String street,
        String city,
        String state,
        String documentType,
        double avgRating,
        int ratingCount,
        Integer userId
) {
}
