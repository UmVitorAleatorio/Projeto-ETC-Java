package domain.profession;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Profession {
    private Integer id;
    private String code;
    private String name;
}
