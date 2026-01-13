package domain.user;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_CLIENT("Client"),
    ROLE_EMPLOYEE("Employee");

    private final String label;

    Role(String label) {
        this.label = label;
    }
}
