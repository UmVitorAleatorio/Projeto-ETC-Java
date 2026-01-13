package domain.person;

import domain.Review.Avaliation;
import domain.document.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person {
    Integer id;
    String name;
    String address;
    Document document;
    Avaliation avaliation;
}
