package domain.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {

    private Integer id;
    private Integer personId;
    private Integer userId;

    @Builder.Default
    private boolean isWorking = false;

    @Builder.Default
    private boolean isActive = true;

    private LocalDateTime createdAt;

    public static Employee create(Integer userId) {
        if (userId == null) throw new IllegalArgumentException("UserId required");

        return Employee.builder()
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .isWorking(false)
                .build();
    }
}
