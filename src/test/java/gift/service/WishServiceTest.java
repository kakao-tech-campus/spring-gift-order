package gift.service;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.vo.Money;
import gift.entity.vo.ProductName;
import gift.exception.WishAlreadyExistsException;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WishServiceTest {

    @Autowired
    private WishService wishService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Member member;
    private Product product;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("test@example.com", "password", "USER"));
        product = productRepository.save(new Product(
                new ProductName("테스트 상품"),
                new Money(10000),
                "test.jpg"
        ));
    }

    @Test
    @DisplayName("위시리스트에 중복된 상품 추가 시 예외 발생 테스트")
    void addDuplicateWish_ThrowsException() {
        wishService.addWish(member, product.getId());

        WishAlreadyExistsException exception = assertThrows(WishAlreadyExistsException.class, () -> {
            wishService.addWish(member, product.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("이미 위시리스트에 추가된 상품입니다.");
    }
}