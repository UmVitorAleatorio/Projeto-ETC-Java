package domain.person;

import domain.service.ServiceOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Client extends Person {
    List<ServiceOrder> servicesRequested;
}