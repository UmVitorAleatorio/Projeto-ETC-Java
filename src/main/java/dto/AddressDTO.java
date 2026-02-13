package dto;

import lombok.Builder;

@Builder
public record AddressDTO(
        Integer id,
        String state,
        String city
) {
}
