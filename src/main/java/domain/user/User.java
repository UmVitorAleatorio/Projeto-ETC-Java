package domain.user;

import domain.person.Client;
import domain.person.Employee;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class User {
    private String email;
    private String password;
    Set<Role> roles;

    Client client;
    Employee employee;
}
