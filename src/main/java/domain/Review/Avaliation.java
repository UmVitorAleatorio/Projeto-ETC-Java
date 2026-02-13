package domain.Review;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Avaliation {

    @Builder.Default
    private double averageRating = 5.0;

    @Builder.Default
    private int ratingCount = 0;

    private void saveRating (int rating) {
        this.ratingCount++;
        this.averageRating = ((this.averageRating * (this.ratingCount - 1)) + rating) / this.ratingCount;
    }

    public void addRating (int rating) {
        this.saveRating(rating);
    }
}
