package domain.user;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class User {
    private Integer id;
    private Integer personId;
    private String email;
    private String password;
    private Set<Role> roles;
}
