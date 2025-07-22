package gift.dto;

import gift.entity.Product;
import java.util.List;
import java.util.stream.Collectors;

public record ProductResponse(
        Long id,
        String name,
        Integer price,
        String imageUrl,
        List<OptionResponse> options
) {
    public static ProductResponse from(Product product) {
        List<OptionResponse> optionResponses = product.getOptions().stream()
                .map(OptionResponse::from)
                .collect(Collectors.toList());
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), optionResponses);
    }
}