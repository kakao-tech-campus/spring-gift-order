package gift.service;

import gift.dto.OptionRequest;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Product;
import gift.entity.vo.Money;
import gift.entity.vo.ProductName;
import gift.exception.ProductNotFoundException;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품과 옵션을 함께 추가하는 기능 성공 테스트")
    void addProductWithOptions_Success() {
        // given
        List<OptionRequest> optionRequests = List.of(
                new OptionRequest("옵션1", 10),
                new OptionRequest("옵션2", 20)
        );
        ProductRequest request = new ProductRequest("새 상품", 10000, "new.jpg", optionRequests);

        // when
        ProductResponse response = productService.addProductWithOptions(request);

        // then
        Product foundProduct = productRepository.findById(response.id()).orElseThrow();

        assertAll(
                () -> assertThat(response.id()).isNotNull(),
                () -> assertThat(response.name()).isEqualTo("새 상품"),
                () -> assertThat(foundProduct.getOptions()).hasSize(2),
                () -> assertThat(foundProduct.getOptions().get(0).getName()).isEqualTo("옵션1")
        );
    }

    @Test
    @DisplayName("존재하지 않는 상품 수정 시 예외 발생 테스트")
    void updateNonExistentProduct_ThrowsException() {
        // given
        Long nonExistentId = 999L; // 존재하지 않는 ID
        List<OptionRequest> optionRequests = List.of(new OptionRequest("옵션", 10));
        ProductRequest request = new ProductRequest("수정 시도", 5000, "update.jpg", optionRequests);

        // when & then
        // 존재하지 않는 ID로 수정을 시도할 때 예외가 발생하는지 검증합니다.
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProductWithOptions(nonExistentId, request);
        });

        assertThat(exception.getMessage()).isEqualTo("해당 ID의 상품을 찾을 수 없습니다: " + nonExistentId);
    }

    @Test
    @DisplayName("상품 ID로 조회 성공 테스트")
    void findProductResponseById_Success() {
        // given
        Product savedProduct = productRepository.save(
                new Product(new ProductName("찾을 상품"), new Money(12345), "find.jpg")
        );

        // when
        ProductResponse response = productService.findProductResponseById(savedProduct.getId());

        // then
        assertThat(response.name()).isEqualTo("찾을 상품");
        assertThat(response.price()).isEqualTo(12345);
    }
}
