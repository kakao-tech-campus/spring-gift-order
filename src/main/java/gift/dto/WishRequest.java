package gift.dto;

import jakarta.validation.constraints.NotNull;

public record WishRequest(
        @NotNull(message = "상품 ID는 비워둘 수 없습니다.")
        Long productId
) {
}
