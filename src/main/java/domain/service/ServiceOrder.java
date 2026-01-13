package domain.service;

import domain.Review.Review;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ServiceOrder {
    Integer id;
    String name;
    String serviceDescription;
    LocalDateTime startDate;
    LocalDateTime endDate;
    List<Review> reviews;
    StatusService status;
}
