package domain.address;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Address {
    private Integer id;
    private State state;
    private String city;
    private String street;
    private String number;
}
