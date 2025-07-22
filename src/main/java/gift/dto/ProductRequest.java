package gift.dto;

import gift.entity.Product;
import gift.entity.vo.Money;
import gift.entity.vo.ProductName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record ProductRequest(
        @NotBlank String name,
        @PositiveOrZero Integer price,
        @NotBlank String imageUrl,
        @NotEmpty(message = "상품에는 최소 하나 이상의 옵션이 있어야 합니다.")
        @Valid List<OptionRequest> options
) {
    public Product toEntity() {
        return new Product(new ProductName(name), new Money(price), imageUrl);
    }
}
