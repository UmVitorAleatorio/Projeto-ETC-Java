package domain.Review;

import domain.service.ServiceOrder;
import domain.user.User;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Builder
@Value
public class Review {
    Integer id;
    User reviewer;
    User reviewee;
    int rating;
    String comment;
    LocalDate reviewDate;
    ServiceOrder service;
}
